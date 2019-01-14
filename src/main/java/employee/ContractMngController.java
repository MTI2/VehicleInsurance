package employee;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db_utils.contract.Contract;
import db_utils.contract.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class ContractMngController
{

    final String extApiUrl = "http://51.145.158.146:4040";
    final String insuranceType = "vehicle";

    Session sessionManager = Session.getInstance();

    @Autowired
    ContractRepository cr;


    //TODO: add 403 on invalid cookie
    /*
    test case:
    INSERT INTO `waitingcontracts` (`ID`, `VehicleYear`, `ClientToken`, `RequestToken`, `PlateNumber`, `Date`, `VIN`, `Model`, `Brand`, `InsurancePcg`, `StorageName`) VALUES (NULL, '123',  'test', 'test', 'test', '2019-01-15', 'test', 'test', 'test',  "AC", 'test');
     */
    @RequestMapping(value = "/contract/reject", method = RequestMethod.POST)
    String reject(@CookieValue(Session.SESSION_COOKIE_NAME) String token,
                  @RequestParam("request_token") String request_token,
                  HttpServletResponse response)
    {
        if (!sessionManager.isSessionExists(token))
        {
            response.setStatus(400);
            return "No session identified by such token!";
        }

        if (request_token.isEmpty())
        {
            response.setStatus(400);
            return "Request token cannot be empty!!";
        }

        Optional<Contract> c = cr.findOneByRequestToken(request_token);

        if (!c.isPresent())
        {
            response.setStatus(400);
            return "No contract with such token!";
        }


        int ok = cr.deleteContractByRequestToken(request_token);

        if (ok < 1)
        {
            response.setStatus(400);
            return "Couldn't delete contract with token: '" + request_token + "'";
        }


        return "OK";

    }


    @RequestMapping(value = "/contract/accept", method = RequestMethod.POST)
    String accept(@CookieValue(Session.SESSION_COOKIE_NAME) String token,
                  @RequestParam("request_token") String request_token,
                  HttpServletResponse response)
    {
        if (!sessionManager.isSessionExists(token))
        {
            response.setStatus(400);
            return "No session identified by such token!";
        }

        if (request_token.isEmpty())
        {
            response.setStatus(400);
            return "Request token cannot be empty!!";
        }

        Optional<Contract> c = cr.findOneByRequestToken(request_token);

        if (!c.isPresent())
        {
            response.setStatus(400);
            return "No contract with such token!";
        }

        Contract ctr = c.get();
        String clientToken = ctr.getClientToken();

        String requestUri = "/client/" + clientToken + "/insurance/vehicle";
        requestUri = extApiUrl + requestUri;
        System.out.println(requestUri);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = null;
        try
        {
            requestJson = mapper.writeValueAsString(ctr);
        } catch (JsonProcessingException e)
        {
            response.setStatus(500);
            return "Conversion to JSON failed! Reason: " + e.getMessage();
        }


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);


      /*  String answer = "{\n" +
                "\"code\": 403,\n" +
                "\"message\": \"Not ok!\",\n" +
                "\"payload\": [\n" +
                " {\n" +
                "\"clientId\": \"aog2910fnig02h-f2h39g72g2-wfeg\",\n" +
                "\"insuranceCost\": 2000,\n" +
                "\"carModel\": \"Passeratti\"\n" +
                " },\n" +
                " {\n" +
                "\"clientId\": \"aog2910fnig02h-f2h39g72g2-wfeg\",\n" +
                "\"insuranceCost\": 2000,\n" +
                "\"carModel\": \"Passeratti\"\n" +
                " }\n" +
                "]\n" +
                "}";*/

        String answer = restTemplate.postForObject(requestUri, entity, String.class);

        if (answer.isEmpty())
        {
            response.setStatus(500);
            return "External API gave no response!";
        }

        HashMap<String, Object> result = null;
        try
        {
            result =
                    mapper.readValue(answer, HashMap.class);
        } catch (IOException e)
        {
            response.setStatus(500);
            return "External API returned invalid JSON!";
        }

        System.out.println(result.get("code").toString());
        if (Integer.parseInt(result.get("code").toString()) != 201)
        {
            response.setStatus(400);
            return "Invalid request! External api, error message: '" + result.get("message").toString() + "'";
        }


        if (cr.deleteContractByRequestToken(request_token) < 1)
        {
            response.setStatus(400);
            return "Couldn't delete contract with token: '" + request_token + "'";
        }

        return "OK";
    }


    @RequestMapping(value = "/contract/details", method = RequestMethod.GET)
    String details(@CookieValue(Session.SESSION_COOKIE_NAME) String token,
                   @RequestParam("request_token") String request_token,
                   HttpServletResponse response)
    {
        if (!sessionManager.isSessionExists(token))
        {
            response.setStatus(400);
            return "No session identified by such token!";
        }

        if (request_token.isEmpty())
        {
            response.setStatus(400);
            return "Request token cannot be empty!!";
        }

        Optional<Contract> c = cr.findOneByRequestToken(request_token);

        if (!c.isPresent())
        {
            response.setStatus(400);
            return "No contract with such token!";
        }



        Contract ctr = c.get();
        String clientToken = ctr.getClientToken();

        String requestUri = "/client/" + clientToken + "/insurance";
        requestUri = extApiUrl + requestUri;

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = null;
        try
        {
            requestJson = mapper.writeValueAsString(ctr);
        } catch (JsonProcessingException e)
        {
            response.setStatus(500);
            return "Conversion to JSON failed! Reason: " + e.getMessage();
        }


        return requestJson;

    }


    String toJSON(List<Contract> r) throws JsonProcessingException
    {
        String [] res = new String[r.size()];

        for(int n=0; n<r.size(); n++)
        {
            res[n] = r.get(n).getClientToken();
        }

        ObjectMapper mapper = new ObjectMapper();
        String json  = mapper.writeValueAsString(res);

        return json;
    }

    HashMap getDate(String dateJSON) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> r = mapper.readValue(dateJSON, HashMap.class);

        HashMap<String, String> result = new HashMap<>();

        for(HashMap.Entry<String, Object> entry : r.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            result.put(key, value);
            // do what you have to do here
            // In your case, another loop.
        }

        if((!result.containsKey("from") && !result.containsKey("to")))
            throw new IOException("One of fields (or both) ['form', 'to'] is mandatory!");




        return result;
    }

    @RequestMapping(value = "/contract/list", method = RequestMethod.GET)
    String list_contrcts(@CookieValue(Session.SESSION_COOKIE_NAME) String token,
                         @RequestParam(value="client_token", required=false) String client_token,
                         @RequestParam(value="date", required=false) String date,
                         HttpServletResponse response)
    {
        String json = "";
        LocalDate from = null;
        LocalDate to = null;

        if (!sessionManager.isSessionExists(token))
        {
            response.setStatus(400);
            return "No session identified by such token!";
        }



        if((client_token == null || client_token.isEmpty()) && (date == null || date.isEmpty()))
        {
            List<Contract> r =  cr.findAll();


            try
            {
                json = toJSON(r);
            } catch (JsonProcessingException e)
            {
                response.setStatus(500);
                return "Conversion to JSON failed! Reason: " + e.getMessage();
            }

        }

        if (date != null && !date.isEmpty())
        {
            HashMap<String, String> dates = null;
            try
            {
                dates = getDate(date);
            } catch (IOException e)
            {
                response.setStatus(400);
                return "Invalid date json provided: '" + e.getMessage()+"'";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try
            {
                if(dates.containsKey("from"))
                from = LocalDate.parse(dates.get("from"), formatter);

                if(dates.containsKey("to"))
                to = LocalDate.parse(dates.get("to"), formatter);
            }
            catch(DateTimeParseException e)
            {
                response.setStatus(400);
                return "Cannot parse date error: '" + e.getMessage()+"'";
            }
        }



        if(client_token != null && !client_token.isEmpty() && (from != null || to != null) )
        {

            List<Contract> r =  null;

            if(from != null && to != null)
                r = cr.findAllByClientTokenAndDateBetween(  client_token,
                                                    java.sql.Date.valueOf(from),
                                                    java.sql.Date.valueOf(to));
            else if (from != null)
                r = cr.findAllByClientTokenAndDateAfter(client_token,  java.sql.Date.valueOf(from));
            else if (to != null)
                r = cr.findAllByClientTokenAndDateBefore(client_token,  java.sql.Date.valueOf(to));

            try
            {
                json = toJSON(r);
            } catch (JsonProcessingException e)
            {
                response.setStatus(500);
                return "Conversion to JSON failed! Reason: " + e.getMessage();
            }

        }

        if( from != null || to != null )
        {
            List<Contract> r =  null;

            if(from != null && to != null)
                r = cr.findAllByDateBetween(java.sql.Date.valueOf(from),
                                            java.sql.Date.valueOf(to));
            else if(from != null)
                r = cr.findByDateAfter( java.sql.Date.valueOf(from));
            else if(to != null)
                r = cr.findByDateBefore( java.sql.Date.valueOf(to));

            try
            {
                json = toJSON(r);
            } catch (JsonProcessingException e)
            {
                response.setStatus(500);
                return "Conversion to JSON failed! Reason: " + e.getMessage();
            }
        }




        if( client_token != null && !client_token.isEmpty() )
        {
            List<Contract> r =  null;


                r = cr.findAllByClientToken(client_token);
            try
            {
                json = toJSON(r);
            } catch (JsonProcessingException e)
            {
                response.setStatus(500);
                return "Conversion to JSON failed! Reason: " + e.getMessage();
            }
        }


        if(json == null || json.isEmpty())
        {
            response.setStatus(400);
            return "Something went horribly wrong!";
        }



        return json;
    }





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


    @RequestMapping(value = "/contract/extend", method = RequestMethod.GET)
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



}