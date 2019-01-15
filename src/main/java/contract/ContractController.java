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
}
