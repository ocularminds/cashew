package cashew.routes;

import cashew.Fault;
import cashew.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Index class that shows availability of the application and running time.
 *
 * @author Festus B. Jejelowo
 * @author mail.festus@gmail.com
 */
@RestController
@RequestMapping(value = {"/auth"})
public class Auth {

    /**
     * Time application started.
     */
    transient LocalDateTime start;
    transient LoginService service;

    public Auth(LoginService loginService) {
        start = LocalDateTime.now();
        this.service = loginService;
    }
    
    @PostMapping
    public Fault authenticate(
        @RequestParam("user") String username, 
        @RequestParam("password") String pwd) {		
		Fault fault = service.login(username, pwd);
		return fault;
    }
}
