package auth;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

public interface IFixAuthentication {
    /**
     * make authentication for BookingUser with UserDetails=userDetails
     * and fix result in session
     *
     * @param userDetails of BookingUser (admin or user)
     * @param request current HttpServletRequest
     */
    public void authenticateUserAndSetSession(UserDetails userDetails,
                                              HttpServletRequest request);
}
