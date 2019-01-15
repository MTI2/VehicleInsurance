package contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db_utils.contract.Contract;
import db_utils.contract.ContractRepository;
import employee.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ContractController {
    final String extApiUrl = "http://51.145.158.146:4040";
	
    final String insuranceType = "vehicle";
    private enum InsuranceTypes
    {
        OC, AC, OC_AC
    }

    @Autowired
    ContractRepository cr;

    @RequestMapping(value = "/contract/check", method = RequestMethod.GET)
    String check(@RequestParam("request_token") String request_token,
                 @RequestParam("client_id") String client_token,
                 HttpServletResponse response)
    {
        if (request_token.isEmpty())
        {
            response.setStatus(400);
            return "Request token cannot be empty!!";
        }
        if (client_token.isEmpty())
        {
            response.setStatus(400);
            return "Client token cannot be empty!!";
        }

        //zapytanie do zewn api
        String requestUri = "/client/" + client_token + "/insurances";
        requestUri = extApiUrl + requestUri;
        System.out.println(requestUri);

        RestTemplate restTemplate = new RestTemplate();
        String answer = restTemplate.getForObject(requestUri, String.class);
        if (answer.isEmpty())
        {
            response.setStatus(500);
            return "External API gave no response!";
        }

        //czy w zewnetrznej bazie jest
        if(answer.contains(request_token)) {
            response.setStatus(200);
            return "{ \"accepted\": \"true\", \"msg\": \"Contract is accepted\" }";
        }

        //sprawdzamy w lokalnej bazie
        Optional<Contract> c = cr.findOneByRequestToken(request_token);

        if (!c.isPresent())
        {
            response.setStatus(200);
            return "{ \"accepted\": \"false\", \"msg\": \"Contract is rejected.\" }";
        }
        else {
            response.setStatus(200);
            return "{ \"accepted\": \"false\", \"msg\": \"Contract is waiting for acceptation.\" }";
        }

    }


    @RequestMapping(value = "/contract/extend", method = RequestMethod.POST)
    String extend(@RequestParam("token") String token,
                  @RequestParam("contract_id") String contract_id,
                  HttpServletResponse response)
    {
        if (token.isEmpty())
        {
            response.setStatus(400);
            return "Request token cannot be empty!!";
        }
        if (contract_id.isEmpty())
        {
            response.setStatus(400);
            return "Contract id cannot be empty!!";
        }

        //pobieramy z zewnetrznego api
        String requestUri = "/insurance/" + contract_id;
        requestUri = extApiUrl + requestUri;
        System.out.println(requestUri);

        RestTemplate restTemplate = new RestTemplate();
        String answer = restTemplate.getForObject(requestUri, String.class);
        if (answer.isEmpty())
        {
            response.setStatus(500);
            return "External API gave no response!";
        }

        //robimy contract z jsona
        ObjectMapper mapper = new ObjectMapper();
        Contract contract = null;
        try {
            contract = mapper.readValue(answer, Contract.class);
        } catch (IOException e) {
            response.setStatus(500);
            return "External API returned invalid JSON!";
        }

        //zabawa z data xDDD
        java.util.Date utilDate = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(utilDate);
        c.add(Calendar.YEAR, 1);
        utilDate = c.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        //czy wygasla
        if(contract.getDate().compareTo(sqlDate) < 0) {
            response.setStatus(200);
            return "Contract did not expire. Cannot extend.";

        }
        contract.setDate(sqlDate);

        //przygotowuje do zapisu w bazie zewnetrznej
        String requestUri2 = "/client/" + contract.getClientToken() + "/insurance/" + contract.getId();
        requestUri = extApiUrl + requestUri;
        System.out.println(requestUri);

        String requestJson = "";
        try
        {
            requestJson = mapper.writeValueAsString(contract);
        } catch (JsonProcessingException e)
        {
            response.setStatus(500);
            return "Conversion to JSON failed! Reason: " + e.getMessage();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

        ResponseEntity<String> resp = restTemplate.exchange(requestUri2, HttpMethod.PUT, entity, String.class);
        answer = resp.getBody();
        if (answer.isEmpty())
        {
            response.setStatus(500);
            return "External API gave no response!";
        }

//        HashMap<String, Object> result = null;
//        try
//        {
//            result = mapper.readValue(answer, HashMap.class);
//        } catch (IOException e)
//        {
//            response.setStatus(500);
//            return "External API returned invalid JSON!";
//        }

//        System.out.println(result.get("code").toString());
//        if (Integer.parseInt(result.get("code").toString()) != 201)
//        {
//            response.setStatus(400);
//            return "Invalid request! External api, error message: '" + result.get("message").toString() + "'";
//        }


        return answer;

    }

	//TODO: add 403 on invalid cookie
    /*
    test case:
    INSERT INTO `waitingcontracts` (`ID`, `VehicleYear`, `ClientToken`, `RequestToken`, `PlateNumber`, `Date`, `VIN`, `Model`, `Brand`, `InsurancePcg`, `StorageName`) VALUES (NULL, '123',  'test', 'test', 'test', '2019-01-15', 'test', 'test', 'test',  "AC", 'test');
     */
    @RequestMapping(value = "/contract/new", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String showNew(@RequestParam("token") String token,
                   HttpServletResponse response)
    {
        String requestUri = "/client/" + token + "/insurances" ;
        requestUri = extApiUrl + requestUri;


        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(requestUri, String.class);


        if(result == null || result.equals(""))
        {
            response.setStatus(400);
            return "Bad request!";
        }

        try
        {
            JSONObject jsonObj = new JSONObject(result);

            System.out.println(jsonObj);
            if(!jsonObj.get("code").equals("200"))
            {
                response.setStatus(403);
                return "No client identified by such token!";
            }
        }
        catch (JSONException e)
        {

        }




        String jsonString = null;
        try
        {
            jsonString = new JSONObject()
                    .put("vehicleYear", new JSONObject()
                            .put("name", "Rok pojazdu")
                            .put("type", "vehicleYear")
                            .put("default", ""))
                    .put("vin", new JSONObject()
                            .put("name", "Jakis vin")
                            .put("type", "vin")
                            .put("default", ""))
                    .put("date", new JSONObject()
                            .put("name", "Jakas data")
                            .put("type", "date")
                            .put("default", ""))
                    .put("model", new JSONObject()
                            .put("name", "Model samochodu")
                            .put("type", "model")
                            .put("default", ""))
                    .put("brand", new JSONObject()
                            .put("name", "Marka? smochodu")
                            .put("type", "brand")
                            .put("default", ""))
                    .put("plateNumber", new JSONObject()
                            .put("name", "Numer rejstracyjny")
                            .put("type", "plateNumber")
                            .put("default", ""))
                    .put("insurancePcg", new JSONObject()
                            .put("name", "Rodzaje ubezpieczen")
                            .put("value", new JSONArray()
                                    .put(InsuranceTypes.OC)
                                    .put(InsuranceTypes.AC)
                                    .put(InsuranceTypes.OC_AC)
                            )
                            .put("type", "insurancePcg")
                            .put("default", ""))
                    .put("storageName", new JSONObject()
                            .put("name", "Rodzaj przechowywania samochodu")
                            .put("type", "storageName")
                            .put("default", "garaz")).toString();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return  jsonString;
    }

    @RequestMapping(value = "/contract/add", method = RequestMethod.POST)
    String add(@RequestParam("token") String token,
               @RequestBody String contract_string,
               HttpServletResponse response)
    {


        JSONObject contract_json = null;
        try
        {
            contract_json = new JSONObject(contract_string);
        }
        catch (JSONException e)
        {

        }

        if(contract_json == null)
        {
            response.setStatus(400);
            return "Bad json format!";
        }

        String requestUri = "/client/" + token + "/insurances" ;
        requestUri = extApiUrl + requestUri;

        System.out.println(requestUri);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(requestUri, String.class);

        System.out.println(result);

        if(result == null || result.equals(""))
        {
            response.setStatus(400);
            return "Bad request!";
        }

        try
        {
            JSONObject jsonObj = new JSONObject(result);

            System.out.println(jsonObj);
            if(!jsonObj.get("code").equals("200"))
            {
                response.setStatus(403);
                return "No client identified by such token!";
            }
        }
        catch (JSONException e)
        {

        }


       long vehicleYear = 0;
       String vin = null;
       String date = null;
       String model = null;
       String brand = null;
       String plateNumber = null;
       String insurancePcg = null;
       String storageName = null;

        try {
            if (!contract_json.has("vehicleYear") || !contract_json.has("vin") || !contract_json.has("date")
                    || !contract_json.has("model") || !contract_json.has("brand") || !contract_json.has("plateNumber")
                    || !contract_json.has("insurancePcg") || !contract_json.has("storageName")) {
                response.setStatus(400);
                return String.valueOf(new IllegalArgumentException("{\"error\":\"At least one parameter is not supplied\"}"));
            }


            if (contract_json.get("vehicleYear") == null || contract_json.get("vin").equals("") || contract_json.get("date").equals("")
                    || contract_json.get("model").equals("") || contract_json.get("brand").equals("") || contract_json.get("plateNumber").equals("")
                    || contract_json.get("insurancePcg").equals("") || contract_json.get("storageName").equals("")) {
                response.setStatus(400);
                return String.valueOf(new IllegalArgumentException("{\"error\":\"At least one parameter is invalid\"}"));
            }

            try
            {
                vehicleYear = Long.parseLong((String) contract_json.get("vehicleYear"));
                vin = (String) contract_json.get("vin");
                date = (String) contract_json.get("date");
                model = (String) contract_json.get("model");
                brand = (String) contract_json.get("brand");
                plateNumber = (String) contract_json.get("plateNumber");
                insurancePcg = (String) contract_json.get("insurancePcg");
                storageName = (String) contract_json.get("storageName");
            }
            catch (Exception e)
            {
                response.setStatus(400);
                return e.toString();
            }

        }
        catch (JSONException e)
        {

        }

        if(!date.matches("(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d)"))
        {

            response.setStatus(400);
            return "Date must be in format dd-MM-yyyy";
        }
        else
        {
            String data[] = date.split("-");

            int day = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1]);
            int year = Integer.parseInt(data[2]);

            if(day <= 0 || day > 31)
            {
                response.setStatus(400);
                return "Day of date must be in range <1,31>";
            }

            if(month <= 0 || month > 12)
            {
                response.setStatus(400);
                return "Month of date must be in range <1,12>";
            }

        }


        HashSet<String> insuranceTypesSet = new HashSet<>();

        for(InsuranceTypes type : InsuranceTypes.values())
        {
            insuranceTypesSet.add(type.name());
        }

        if(!insuranceTypesSet.contains(insurancePcg))
        {
            response.setStatus(400);
            return "InsurancePcg must be on of " + insuranceTypesSet.toString() ;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        Contract contract = new Contract();

        contract.setClientToken(token);

        Date dateFormatted = Date.valueOf(LocalDate.parse(date, formatter));

        System.out.println(dateFormatted);

        contract.setVehicleYear(vehicleYear);
        contract.setVin(vin);
        contract.setDate(dateFormatted);
        contract.setModel(model);
        contract.setBrand(brand);
        contract.setPlateNumber(plateNumber);
        contract.setInsurancePcg(insurancePcg);
        contract.setStorageName(storageName);


        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String requestToken = encoder.encodeToString(bytes);

        contract.setRequestToken(requestToken);


        cr.save(contract);

        return requestToken;
    }


}
