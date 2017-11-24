package controller;

import auth.FixAuthentication;
import auth.IFixAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.Admin;
import services.IAdminService;
import services.auth.IUserDetailsService;
import services.auth.impl.Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/login-admin")
public class LoginAdminController {
    private static Logger log = Logger.getLogger(LoginAdminController.class);
    private static final String LOGIN_ADMIN_PAGE = "login_admin/page";

    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserDetailsService userDetailsService;
    @Autowired
    private IFixAuthentication fixAuthentication;


    @RequestMapping(value="/page", method = {RequestMethod.GET,RequestMethod.POST})
    public String loginAdminPage(ModelMap model,
                                 @ModelAttribute(value = "admin") Admin admin, HttpServletRequest request) throws UnsupportedEncodingException {
        final Long  DEFAULT_ID = -1L;

        // del after debug
        System.out.println("->: LoginAdminController.loginAdminPage. Admin: " + admin);

        // check valid income admin
        admin = admin == null ? new Admin() : admin;

        // adaptation to Cyrillic admin name and password
        if (admin.getName() != null) {
            admin.setName(FixAuthentication.adaptationToCyrillic(admin.getName()));
        }
        if (admin.getPassword() != null) {
            admin.setPassword( FixAuthentication.adaptationToCyrillic(admin.getPassword()) );
        }

        System.out.println("-> after adaptation to Cyrillic. admin: " + admin);

        //validate admin parameters
        admin.setId(admin.getId() != null ? admin.getId() : DEFAULT_ID);
        Admin adminFromDb = adminService.get(admin.getId());
        boolean validParam = adminFromDb != null
                && adminFromDb.getId().equals(admin.getId())
                && adminFromDb.getName().equals(admin.getName())
                && adminFromDb.getPassword().equals(Encoder.encode(admin.getPassword()));

        // del after debug
        if (adminFromDb != null) {
            System.out.println("adminFromDb.getId().equals(admin.getId()): " + adminFromDb.getId().equals(admin.getId()));
            System.out.println("adminFromDb.getName().equals(admin.getName()): " + adminFromDb.getName().equals(admin.getName()));
            System.out.println("adminFromDb.getPassword().equals(Encoder.encode(admin.getPassword()): "
                    + adminFromDb.getPassword().equals(Encoder.encode(admin.getPassword())));
        }
        System.out.println("validParam: " + validParam);

        if (validParam) {
            // valid parameters set admin LogOn in session
            request.getSession().setAttribute("admin", adminFromDb);
            request.getSession().setAttribute("user", null);

            // set Spring security authentication !!!!!!!!!
            fixAuthentication
                    .authenticateUserAndSetSession(userDetailsService.getUserDetailsForAdmin(adminFromDb),
                            request);

            // refer to admin work "redirect:/orders_admin/page"
            log.info("Successfully login Admin: " + adminFromDb);

            return "redirect:/orders_admin/page";

        } else {
            // invalid  parameters
            if ( ! DEFAULT_ID.equals(admin.getId()) || admin.getName() != null
                    || admin.getPassword() != null) {
                //save errorMsg in request
                model.addAttribute("errorMsg", "Invalid parameters, try again.");
            }
            admin.setId(DEFAULT_ID.equals(admin.getId()) ? null : admin.getId() );
            //save admin in request
            fillModel(model, admin);

            //refer to try again login, -> LOGIN_ADMIN_PAGE
            log.info("Invalid  Admin parameters: " + admin);

            return LOGIN_ADMIN_PAGE;
        }

    }

    private void fillModel(ModelMap model, Admin admin) {
        populatePageName(model);
        model.addAttribute("admin", admin != null ? admin : new Admin());
    }

    private void populatePageName(ModelMap model) {
        model.addAttribute("currentPageName", "login-admin");
        model.addAttribute("currentTitle", "page.login_admin.title");
    }
}
