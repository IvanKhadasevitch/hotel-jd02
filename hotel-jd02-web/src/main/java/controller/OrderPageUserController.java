package controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.Order;
import pojos.RoomType;
import pojos.User;
import services.IOrderService;
import services.IUserService;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

// как она работает ?????
//@SessionAttributes(value = {"outOrder"}, types = {Order.class})
@Controller
@RequestMapping("/orders_user")
public class OrderPageUserController {
    private static Logger log = Logger.getLogger(OrderPageUserController.class);

    private static final String ORDERS_USER = "orders_user/main";
    private static final String ORDERS_USER_SHOW_ORDER = "orders_user/showOrder";
    private static final String ORDERS_USER_LOOK_ORDERS = "orders_user/lookOrders";

    private IOrderService orderService;
    private IUserService userService;

    @Autowired
    public OrderPageUserController(IOrderService orderService, IUserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @Transactional
    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String mainPage(ModelMap model, Order order, HttpSession session) {
        // del after debug
        System.out.println("->: OrderPageUserController.mainPage. Order: " + order);

        // check in session before done order
        if (session.getAttribute("outOrder") != null) {
            // clear previous order
            order = null;
            session.setAttribute("outOrder", null);
        }

        // check valid Hotel choice
        RoomType roomType = (RoomType) session.getAttribute("roomType");
        if (roomType == null || roomType.getId() == null || roomType.getId() <= 0) {
            // roomType==null or roomTypeId not exist, choose roomType please
            fillModel(model, order);
            // save in request error Hotel message
            model.addAttribute("errorMsgHotel", "Invalid Hotel");
            log.info("Select first hotel before order. Hotel [null]");

            return ORDERS_USER;
        }

        // check valid request param
        Date currentDate = new Date((new java.util.Date()).getTime());
        Date arrivalDate = order != null && order.getArrivalDate() != null
                ? order.getArrivalDate()
                : currentDate;
        Date eventsDate = order != null && order.getEventsDate() != null
                ? order.getEventsDate()
                : currentDate;

        boolean isWrongDate = false;
        if (arrivalDate.before(currentDate) || eventsDate.before(currentDate)
                || eventsDate.before(arrivalDate) || arrivalDate.equals(eventsDate)) {
            isWrongDate = true;
            if (order != null && order.getArrivalDate() != null && order.getEventsDate() != null) {
                // save in request error Dates message
                model.addAttribute("errorMsgDates", "Invalid Dates");
                log.info(String.format("User choose incorrect dates - arrivalDate [%s] or " +
                        "eventsDate [%s]. Dates must be greater than the current date [%s] and " +
                        "eventsDate greater than the arrivalDate", arrivalDate, eventsDate, currentDate));
            }
        }

        // isWrongDate or first form visit
        if (isWrongDate || order == null) {
            // del after debug
            System.out.println("->: OrderPageUserController.mainPage. WrongDate. Order: " + order);

            // WrongDate, refer to try again choose order dates. ORDERS_USER
            fillModel(model, order);

            return ORDERS_USER;
        }

        // make order, save in DB
        User user = (User) session.getAttribute("user");
        user = userService.get(user.getId());         // add user to persistence context

        Order outOrder = orderService.make(roomType.getId(), arrivalDate, eventsDate);
        outOrder.setUser(user);

        outOrder = orderService.add(outOrder);    // add outOrder to persistence context and DB
        session.setAttribute("outOrder", outOrder);       //save outOrder in session

        // refer to view Order, to order/user/showOrder.jsp
        log.info("Order successfully created. Order: " + outOrder);

        return "redirect:/orders_user/showOrder";
    }

    @RequestMapping(value = "/showOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public String showOrderPage(ModelMap model) {
        populatePageTitle(model);
        model.addAttribute("currentPageName", "orders_user/showOrder");
        // refer to view Order, to order/user/showOrder.jsp
        return ORDERS_USER_SHOW_ORDER;
    }

    @RequestMapping(value = "/lookOrders", method = RequestMethod.GET)
    public String lookOrdersPage(ModelMap model, HttpSession session) {
        final Long DEFAULT_ID = -1L;

        // take current User from session to determine user id
        User user = session.getAttribute("user") != null
                ? (User) session.getAttribute("user")
                : null;
        Long userId = user != null ? user.getId() : DEFAULT_ID;

        fillModel(model, userId);
        // refer to view look Orders, to order/user/lookOrders.jsp
        return ORDERS_USER_LOOK_ORDERS;
    }

    private void fillModel(ModelMap model, Order order) {
        populatePageTitle(model);
        model.addAttribute("currentPageName", "orders_user");
        model.addAttribute("order", order == null ? new Order() : order);
    }

    private void fillModel(ModelMap model, Serializable userId) {
        populatePageTitle(model);
        model.addAttribute("currentPageName", "orders_user/lookOrders");
        List<Order> orderList = orderService.getAllForUserId(userId);
        model.addAttribute("orderList", orderList);
    }

    private void populatePageTitle(ModelMap model) {
        model.addAttribute("currentTitle", "page.orders.title");
    }
}
