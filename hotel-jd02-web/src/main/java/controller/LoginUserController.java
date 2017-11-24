package controller;

import auth.IFixAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.User;
import services.IUserService;
import services.auth.IUserDetailsService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/login-user")
public class LoginUserController {
    private static Logger log = Logger.getLogger(LoginUserController.class);
    private static final String LOGIN_USER = "login_user/page";

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDetailsService userDetailsService;
    @Autowired
    private IFixAuthentication fixAuthentication;


    // first time come to form
    @RequestMapping(value="/page", method = RequestMethod.GET)
    public String loginUserFirstVisit(ModelMap model, @ModelAttribute(name="user") User user) {
        // del after debug
        System.out.println("->: LoginUserController.loginUserFirstVisit. User: " + user);

        fillModel(model, user);
        return LOGIN_USER;
    }

    @RequestMapping(value="/page", method = RequestMethod.POST)
    public String loginUserPage(ModelMap model, User user,
                                HttpServletRequest request) {
        // del after debug
        System.out.println("->: LoginUserController.loginUserPage. User: " + user);

        // check valid income param
        user = user != null ? user : new User();

        String email = user.getEmail();
        // check valid email
        boolean isValidEmail = userService.validateEmail(email);

        User outUser = null;
        if (isValidEmail) {
            // take user from DB by email
            outUser = userService.getUserByEmail(email);
        }

        // invalid email or not user in DB -> try again enter email
        if ( ! isValidEmail || outUser==null) {
            // safe user & errorMsg in request
            fillModel(model, user);
            model.addAttribute("errorMsg", "Invalid email");

            return LOGIN_USER;
        }

        // login user -> refer forward to make orders
        // save user in session
        request.getSession().setAttribute("user", outUser);
        request.getSession().setAttribute("admin", null);

        // set Spring security authentication !!!!!!!!!
        fixAuthentication
                .authenticateUserAndSetSession(userDetailsService.getUserDetailsForUser(outUser),
                        request);

        log.info("Successfully login User: " + outUser);

        return "redirect:/orders_user/page";
    }

    private void fillModel(ModelMap model, User user) {
        populatePageName(model);
        model.addAttribute("user", user != null ? user : new User());
    }

    private void populatePageName(ModelMap model) {
        model.addAttribute("currentPageName", "login-user");
        model.addAttribute("currentTitle", "page.welcome_user.title");
    }
}
