package employee;


import db_utils.employee.Employee;
import db_utils.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value="/employee")
public class EmployeeController
{
        Session sessionManager = Session.getInstance();

        @Autowired
        EmployeeRepository er;
//test case: INSERT INTO `employees` (`ID`, `Name`, `Surname`, `PhoneNumber`, `Mail`, `Login`, `Hash`) VALUES (NULL, 'test', 'test', '123', 'test@test.com', 'test', '$2a$10$o0VlkerJz0OrR1Yets4idOgBcm.B36zW4n7LtvfEfKqZU1fGJH7kC');
        @RequestMapping(value = "/login", method = RequestMethod.POST)
        String login(@RequestParam("login") String login,
                     @RequestParam("password") String password,
                     HttpServletResponse response)
        {

            if(login.isEmpty())
            {
                response.setStatus(400);
                return "Login cznnot be empty!";
            }

            if(password.isEmpty())
            {
                response.setStatus(400);
                return "Password cannot be empty!";
            }



            Employee e = er.getByLogin(login);
            if(e == null)
            {
                response.setStatus(400);
                return "No such user!";
            }

            if(!BCrypt.checkpw(password, e.getHash()))
            {
                response.setStatus(400);
                return "Invalid password!";
            }

            Cookie c = new Cookie(Session.SESSION_COOKIE_NAME,
                                  sessionManager.newSessionToken(e.getId()));
            c.setMaxAge(-1);
            c.setPath("/");
            response.addCookie(c);

            return "OK";
        }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    String logout(@CookieValue(Session.SESSION_COOKIE_NAME) String token,
                 HttpServletResponse response)
    {


       if(!sessionManager.isSessionExists(token))
       {
           response.setStatus(400);
           return "No session identified by such token!";
       }

       sessionManager.removeSession(token);

        return "OK";
    }

}
