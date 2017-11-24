package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import pojos.RoomType;
import services.IRoomTypeService;


@Controller
@SessionAttributes(value = {"roomType"}, types = {RoomType.class})
@RequestMapping("/")
public class RootController {
    private static Logger log = Logger.getLogger(RootController.class);
    private static final String MAIN = "hotels/main";

    private IRoomTypeService roomTypeService;

    @Autowired
    public RootController(IRoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    // first time come to form
    @RequestMapping(value={"/page", ""}, method = RequestMethod.GET)
    public String mainPageFirstVisit(ModelMap model, RoomType roomType) {
        // del after debug
        System.out.println("->: RootController.mainPageFirstVisit. RoomType: " + roomType);
        System.out.println("-> SecurityContextHolder.getContext().getAuthentication():" + SecurityContextHolder.getContext().getAuthentication());

        fillModel(model, roomType);
        return MAIN;
    }

    @RequestMapping(value = {"/page", ""}, method = RequestMethod.POST)
    public String mainPage(ModelMap model, RoomType roomType) {
        // del after debug
        System.out.println("->: RootController.mainPage. RoomType: " + roomType);
        System.out.println("-> SecurityContextHolder.getContext().getAuthentication():" + SecurityContextHolder.getContext().getAuthentication());


        // check exist roomType in DB
        RoomType outRoomType = null;
        Long roomTypeId = null;
        if (roomType != null) {
            roomTypeId = roomType.getId();
            outRoomType = roomTypeService.get(roomTypeId);
        }


        if (outRoomType == null) {
            // no RoomType in DB -> refer again choose RoomType
            log.info("No RoomType with id [" + roomTypeId + "] in DB. Refer again choose RoomType");
            return MAIN;
        } else {
            // chosen valid RoomType -> refer forward User make Orders
            log.info("Chosen valid RoomType with id [" +
                    roomTypeId + "]. Refer User make Orders for RoomType: " + outRoomType);
            // save outRoomType in session
            model.addAttribute("roomType", outRoomType);

            return "redirect:/orders_user/page";
        }
    }

//    @RequestMapping(value = "/access_denied", method = RequestMethod.GET)
//    public String accessDeniedPage(ModelMap model) {
//        fillModel(model);
//        model.addAttribute("user", getPrincipal());
//        return MAIN;
//    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        HttpSession session = request.getSession();
        // del from session user @ admin if exist eny
        session.setAttribute("admin", null);
        session.setAttribute("user", null);


        return "redirect:/page";
    }

    private String getPrincipal(){
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    private void fillModel(ModelMap model, RoomType roomType) {
        populatePageName(model);
        model.addAttribute("roomType", roomType == null ? new RoomType() : roomType);
        model.addAttribute("roomTypeList", roomTypeService.getAll());
    }

    private void populatePageName(ModelMap model) {
        model.addAttribute("currentPageName", "hotels");
        model.addAttribute("currentTitle", "page.hotels.title");
    }
}



