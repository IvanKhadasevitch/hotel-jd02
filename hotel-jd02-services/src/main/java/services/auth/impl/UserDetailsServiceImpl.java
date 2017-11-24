package services.auth.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pojos.Admin;
import pojos.User;
import pojos.enums.BookingUserRoleType;
import services.auth.IUserDetailsService;


import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements IUserDetailsService {
    /**
     * @param admin successfully logIn Admin
     * @return org.springframework.security.core.userdetails.UserDetails
     * for successfully logIn Admin or null if admin=null
     */
    @Override
    public UserDetails getUserDetailsForAdmin(Admin admin) {
        if (admin == null) {
            return null;
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(String.valueOf(BookingUserRoleType.ADMIN)));

        return  new BookingUser(admin.getName(), admin.getPassword(),
                true, true, true, true,
                grantedAuthorities, admin.getName(), "");
    }

    /**
     * @param user successfully logIn User
     * @return org.springframework.security.core.userdetails.UserDetails
     * for successfully logIn User or null if user=null
     */
    @Override
    public UserDetails getUserDetailsForUser(User user) {
        if (user == null) {
            return null;
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(String.valueOf(BookingUserRoleType.SIMPLE_USER)));

        return  new BookingUser(user.getEmail(), user.getPassword(),
                true, true, true, true,
                grantedAuthorities, user.getName(), user.getSurname());
    }
}
