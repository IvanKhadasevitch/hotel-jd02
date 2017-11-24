package services.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pojos.Admin;
import pojos.User;

@Service
public interface IUserDetailsService {
    /**
     *
     * @param admin successfully logIn Admin
     * @return org.springframework.security.core.userdetails.UserDetails
     * for successfully logIn Admin or null if admin=null
     */
    UserDetails getUserDetailsForAdmin(Admin admin);

    /**
     *
     * @param user successfully logIn User
     * @return org.springframework.security.core.userdetails.UserDetails
     * for successfully logIn User or null if user=null
     */
    UserDetails getUserDetailsForUser(User user);
}
