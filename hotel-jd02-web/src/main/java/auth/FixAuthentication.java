package auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Component
public class FixAuthentication implements IFixAuthentication {
    @Autowired
    @Qualifier(value = "myAuthenticationManager")
    private AuthenticationManager authenticationManager;

    public void authenticateUserAndSetSession(UserDetails userDetails,
                                                     HttpServletRequest request) {

        // make spring AuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                        userDetails.getPassword(), userDetails.getAuthorities());
        System.out.println("Line Authentication 1");

        // setDetails of request to spring AuthenticationToken
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
        System.out.println("Line Authentication 2");

        Authentication authenticatedUser = authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);
        System.out.println("Line Authentication 3");

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            System.out.println("Line Authentication 4");
        }

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        System.out.println("Line Authentication 4");

        request.getSession()
               .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                       SecurityContextHolder.getContext());     // creates context for that session.

        System.out.println("Line Authentication 5");

    }
    public static String adaptationToCyrillic(String stringCp1251) throws UnsupportedEncodingException {
        if (stringCp1251 == null) {
            return null;
        } else {
            return  new String(stringCp1251.getBytes("ISO-8859-1"), "UTF-8");
        }
    }
}
