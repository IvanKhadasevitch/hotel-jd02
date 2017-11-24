package services.auth.impl;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Principal;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Getter @Setter @ToString
public class BookingUser extends User implements Principal {

    private String firstName;
    private String lastName;
    private String displayName;

    public BookingUser(String username, String password, boolean enabled,
                       boolean accountNonExpired, boolean credentialsNonExpired,
                       boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities,
                       String firstName, String lastName) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        this.firstName = firstName;
        this.lastName = lastName;
        populateDisplayName();
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    @Override
    public String getName() {
        return getUsername();
    }

    private void populateDisplayName() {
        String name = (firstName + " " + lastName).trim();
        displayName = !StringUtils.isBlank(name) ? name : getUsername();
    }
}
