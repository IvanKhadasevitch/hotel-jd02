package controller;

import auth.FixAuthentication;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.*;
import pojos.enums.OrderStatusType;
import services.IAdminService;
import services.IOrderService;
import services.IRoomService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/orders_admin")
public class OrderPageAdminController {
    private static Logger log = Logger.getLogger(OrderPageAdminController.class);

    private static final String ORDERS_ADMIN_MAIN = "orders_admin/main";
    private final OrderStatusType DEFAULT_ORDER_STATUS = OrderStatusType.NEW;
    private final Serializable DEFAULT_ID = -1L;

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private IAdminService adminService;

    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String mainPage(ModelMap model, HttpServletRequest req) {
        // del after debug
        System.out.println("->: OrderPageAdminController.mainPage. req: " + req);

        // check valid order status, if invalid then  OrderStatusType.NEW
        String orderStatusStr = req.getParameter("orderStatus") != null
                ? (String) req.getParameter("orderStatus")
                : null;
        OrderStatusType orderStatus;
        try {
            orderStatus = OrderStatusType.valueOf(orderStatusStr);
        } catch (NullPointerException | IllegalArgumentException e) {
            orderStatus = DEFAULT_ORDER_STATUS;
        }

        // take all Orders to current hotel with status = orderStatus from DB,
        Hotel hotel = getCurrentHotel(req);
        Serializable hotelId = hotel != null ? hotel.getId() : DEFAULT_ID;

        // fillModel, save data in request
        fillModel(model, orderStatus, hotelId);

        // refer to ORDERS_ADMIN_MAIN
        log.info(String.format("Admin choose orders with status [%s] for hotel with id [%s]",
                orderStatus, hotelId) );

        return ORDERS_ADMIN_MAIN;
    }

    @Transactional
    @RequestMapping(value = "/approveOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public String approveOrderPage(ModelMap model, HttpServletRequest req) throws UnsupportedEncodingException {
        // del after debug
        System.out.println("->: OrderPageAdminController.approveOrderPage. req: " + req);

        // check in data
//        final long DEFAULT_ID = -1L;
        final String DECLINE_ORDER_EN = "Decline";
        final String DECLINE_ORDER_RU = "Отклонить";
        final String DEFAULT = "NotDefine";

        // check decline
        String declineStr = req.getParameter("decline") == null
                ? DEFAULT
                : FixAuthentication.adaptationToCyrillic(req.getParameter("decline"));

        // del after debug
        System.out.println("->:check decline. declineStr: " + declineStr);

        boolean isDecline = DECLINE_ORDER_EN.equals(declineStr) || DECLINE_ORDER_RU.equals(declineStr);

        // del after debug
        System.out.println("->:check decline. isDecline: " + isDecline);

        // take orderId from request
        Serializable orderId = NumberUtils.toLong(req.getParameter("orderId"), (Long) DEFAULT_ID);
        // save orderId in request
        model.addAttribute("orderId", orderId);

        // check isValidOrderId
        Order order = orderService.get(orderId);
        Hotel hotel = getCurrentHotel(req);
        Serializable hotelId = hotel == null
                ? DEFAULT_ID
                : hotel.getId();
        Serializable roomId = order != null && order.getRoom() != null
                ? order.getRoom().getId()
                : DEFAULT_ID;
        Room room = roomService.get(roomId);
        boolean isValidOrderId = order != null &&
                room != null && hotelId.equals(room.getRoomType().getHotel().getId());

        // del after debug
        System.out.println("->:check isValidOrderId: " + isValidOrderId);

        // invalid OrderId tray again, refer to ORDERS_ADMIN_MAIN
        if ( ! isValidOrderId ) {
            //save message in request
            model.addAttribute("orderIdErrorMsg", "Invalid order id");
            fillModel(model,  DEFAULT_ORDER_STATUS,  hotelId);

            // refer to ORDERS_ADMIN_MAIN
            log.info("Invalid Order Id: " + orderId);

            return ORDERS_ADMIN_MAIN;
        }

        Admin admin = getCurrentAdmin(req);
        // set admin in persistence context
        if (admin != null) {
            admin = adminService.get(admin.getId());
        }
        // Decline Order
        if (isDecline) {
            // del after debug
            System.out.println("->: Decline Order: isDecline" + isDecline);

            // try decline order in DB
            if ( ! orderService.decline(orderId, admin)) {
                // del after debug
                System.out.println("->: can't decline order");

                // can't decline order, save message in request
                model.addAttribute("orderDeclineErrorMsg", "Can't decline order");
            }

            fillModel(model,  DEFAULT_ORDER_STATUS,  hotelId);
            // refer to ORDERS_ADMIN_MAIN
            return ORDERS_ADMIN_MAIN;
        }

        // if Not one did't work -> Approve order
        Bill bill = orderService.approve(orderId, admin);

        // del after debug
        System.out.println("->: if Not one did't work -> Approve order. Bill: " + bill);

        if (bill == null) {
            // del after debug
            System.out.println("->: can't approve Order & make Bill. Bill: " + bill);

            // can't approve Order & make Bill,save message in request
            model.addAttribute("orderFreeRoomErrorMsg", "Can't approve order and make bill");
        } else {
            // Order approved & Bill created,
            //save message in request
            model.addAttribute("orderApproveMsg", "Bill dane. Approved order with id");

            log.info(String.format("Bill with id [%s] dane. Approved order with id [%s]",
                    bill.getId(), orderId) );
        }

        fillModel(model,  DEFAULT_ORDER_STATUS,  hotelId);
        //  refer to ORDERS_ADMIN_MAIN, Continue to approve orders
        return ORDERS_ADMIN_MAIN;
    }
    private void fillModel(ModelMap model, OrderStatusType orderStatus, Serializable hotelId) {
        populatePageTitle(model);

        Date currentDate = new Date((new java.util.Date()).getTime());

        // save in request: currentDate, orderList, currentPageName
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("currentPageName", "orders_admin");
        List<Order> orderList = orderService.getAllForStatusAndHotel(orderStatus, hotelId);
        model.addAttribute("orderList", orderList);

        //if orderType NEW let orders approve
        model.addAttribute("isNewOrder", OrderStatusType.NEW.equals(orderStatus));
    }

    private void populatePageTitle(ModelMap model) {
        model.addAttribute("currentTitle", "page.orders.title");
    }

    private Admin getCurrentAdmin (HttpServletRequest req) {
        return  req.getSession().getAttribute("admin") != null
                ? (Admin) req.getSession().getAttribute("admin")
                : null;
    }
    private Hotel getCurrentHotel(HttpServletRequest req) {
        Admin admin = getCurrentAdmin(req);

        return admin != null ? admin.getHotel() : null;
    }
}
