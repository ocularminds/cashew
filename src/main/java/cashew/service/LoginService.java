package cashew.service;

import cashew.Constant;
import cashew.Fault;
import cashew.error.AuthorizationError;
import cashew.error.InvalidParamsException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class LoginService {

    /**
     * Use Bcrypt for password encryption
     */
    public Fault login(String user, String password) throws AuthorizationError{
        if(user == null || user.isEmpty()){
            throw new InvalidParamsException("Unknown user credentials.");
        }

        if(password == null || password.isEmpty()){
            throw new InvalidParamsException("Unknown user credentials.");
        }

        if(user.equals(Constant.IN_MEMORY_USER) && password.equals(Constant.IN_MEMORY_PASS)) {            
		   return new Fault("00", "Success", createToken(user))	;	
        }
        throw new AuthorizationError("Unknown user credentials.");
    }

	private String createToken(String username) {
		List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		String token = Jwts
            .builder()
            .setId(Constant.JWT_TOKEN_ID)
            .setSubject(username)
            .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 600000))
            .signWith(SignatureAlgorithm.HS512, Constant.SECRET.getBytes()).compact();

		return token;
	}
}