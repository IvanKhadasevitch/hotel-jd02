package auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pojos.Admin;
import pojos.User;
import services.auth.IUserDetailsService;

import javax.servlet.http.HttpSession;

@Component(value = "myAuthenticationManager")
@Qualifier(value = "myAuthenticationManager")
public class MyAuthenticationManager implements AuthenticationManager {
    @Autowired
    private HttpSession session;
    @Autowired
    private IUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // check admin in session
        Admin admin = session.getAttribute("admin") != null
                ? (Admin) session.getAttribute("admin")
                : null;
        if (admin != null) {
            // authenticate spring Authentication as Admin
            UserDetails userDetails = userDetailsService.getUserDetailsForAdmin(admin);

            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                    userDetails.getPassword(), userDetails.getAuthorities());
        }

        // check user in session
        User user = session.getAttribute("user") != null
                ? (User) session.getAttribute("user")
                : null;
        if (user != null) {
            // authenticate spring Authentication as User=SIMPLE_USER
            UserDetails userDetails = userDetailsService.getUserDetailsForUser(user);

            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                    userDetails.getPassword(), userDetails.getAuthorities());
        }

        throw new BadCredentialsException("Bad Credentials. No [user] or [admin] are saved in session");
    }
}
