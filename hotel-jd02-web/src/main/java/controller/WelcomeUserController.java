package controller;

import auth.FixAuthentication;
import auth.IFixAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.User;
import services.IUserService;
import services.auth.IUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/welcome-user")
public class WelcomeUserController {
    private static Logger log = Logger.getLogger(WelcomeUserController.class);
    private static final String WELCOME_USER = "welcome_user/page";

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDetailsService userDetailsService;
    @Autowired
    private IFixAuthentication fixAuthentication;


    // first time come to form
    @RequestMapping(value="/page", method = RequestMethod.GET)
    public String registrationUserFirstVisit(ModelMap model, User user) {
        // del after debug
        System.out.println("->: WelcomeUserController.registrationUserFirstVisit. User: " + user);

        fillModel(model, user);
        return WELCOME_USER;
    }

    @RequestMapping(value="/page", method = RequestMethod.POST)
    public String registrationUserPage(ModelMap model, User user,
                                       HttpServletRequest request) throws IOException {
        final String DEFAULT = "NotDefine";

        // del after debug
        System.out.println("->: WelcomeUserController.registrationUserPage. User: " + user);

        // adaptation to Cyrillic user name and surname
        if (user.getName() != null) {
            user.setName(FixAuthentication.adaptationToCyrillic(user.getName()));
        }
        if (user.getSurname() != null) {
            user.setSurname( FixAuthentication.adaptationToCyrillic(user.getSurname()) );
        }

        // del after debug
        System.out.println("->: WelcomeUserController.registrationUserPage. User after adaptation to Cyrillic: " + user);

        String email = user.getEmail();
        // check valid email
        boolean isValidEmail = userService.validateEmail(email);

        // check exist email in DB
        boolean isEmailInDB = false;
        if (isValidEmail) {
            isEmailInDB = userService.existWithEmail(email);
        }

        // invalid email or exist email in DB -> try again enter email
        if ( ! isValidEmail || isEmailInDB ) {
            // safe user & errorMsg in request
            fillModel(model, user);
            model.addAttribute("errorMsg", "Invalid email");

            return WELCOME_USER;
        }

        // registration user OK -> refer forward to make orders

        // del wen user will be use password !!!!!!!!!!
        user.setPassword(DEFAULT);

        // save user in session & DB
        User outUser = userService.add(user);
        String fullName = outUser.getName() != null
                ? outUser.getName()
                : "";
        fullName = outUser.getSurname() != null
                ? (fullName + " " + outUser.getSurname())
                : fullName;
        outUser.setFullName(fullName);
        request.getSession().setAttribute("user", outUser);
        request.getSession().setAttribute("admin", null);

        // set Spring security authentication !!!!!!!!!
        fixAuthentication
                .authenticateUserAndSetSession(userDetailsService.getUserDetailsForUser(outUser),
                        request);

        log.info("Successfully registered user: " + outUser);

//        return "forward:/orders_user/page";
        return "redirect:/orders_user/page";
    }

    private void fillModel(ModelMap model, User user) {
        populatePageName(model);
        model.addAttribute("user", user != null ? user : new User());
    }

    private void populatePageName(ModelMap model) {
        model.addAttribute("currentPageName", "welcome-user");
        model.addAttribute("currentTitle", "page.login_user.title");
    }
}
