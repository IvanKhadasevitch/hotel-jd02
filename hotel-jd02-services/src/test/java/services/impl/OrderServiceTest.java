package services.impl;

import dao.IRoomDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.*;
import pojos.enums.BillStatusType;
import pojos.enums.OrderStatusType;
import services.IAdminService;
import services.IBillService;
import services.IOrderService;
import services.IUserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional
public class OrderServiceTest {
    @Autowired
    IOrderService orderService;

    @Autowired
    IBillService billService;

    @Autowired
    IAdminService adminService;

    @Autowired
    IUserService userService;

    @Autowired
    IRoomDao roomDao;

    @Test
    public void make() {
        Order order = orderService.make(1L, Date.valueOf("1970-01-01"),
                Date.valueOf("1970-01-02"));

        assertNotNull("No order make", order);
        assertEquals("Don't determine room type",
                Long.valueOf(1L), order.getRoom().getRoomType().getId());
        assertEquals("ArrivalDate don't determine in order",  Date.valueOf("1970-01-01"),
                order.getArrivalDate());
        assertEquals("EventsDate don't determine in order", Date.valueOf("1970-01-02"),
                order.getEventsDate());
        assertEquals("Don't determine total", Long.valueOf(3000L),order.getTotal());
    }

    @Test
    @Rollback(value = false)
    public void decline() {
        Admin admin = adminService.get(1L);

        //Can't decline order. No such order in DB
        assertFalse("Decline order, that Not exist in DB",
                orderService.decline(-1L, admin));

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        order.setAdmin(admin);

        User user = userService.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // make new Bill
        Bill bill = new Bill();
        bill.setOrder(order);
        order.setBill(bill);

        bill.setUser(user);
        Room room = roomDao.getAnyForRoomType(1L);
        bill.setRoom(room);
        bill.setArrivalDate(order.getArrivalDate());
        bill.setEventsDate(order.getEventsDate());
        bill.setStatus(BillStatusType.PAID);

        // save Order in Db, Bill will be saved too by cascade
        Order addOrder = orderService.add(order);
        Bill addBill = addOrder.getBill();

        // try decline APPROVED order
        assertFalse("decline APPROVED order",
                orderService.decline(addOrder.getId(), addOrder.getAdmin()));

        // set addOrder status OrderStatusType.NEW
        addOrder.setStatus(OrderStatusType.NEW);
        Order newOrder =  orderService.update(addOrder);

        // decline newOrder
        assertTrue("Can't decline NEW order",
                orderService.decline(newOrder.getId(), adminService.get(2L)));

        // check correctness of decline
        Order declineOrder = orderService.get(newOrder.getId());
        assertEquals("Decline order has not DECLINE status",
                OrderStatusType.DECLINE, declineOrder.getStatus());
        assertEquals("Admin id defer from admin id who decline order", Long.valueOf(2L),
                declineOrder.getAdmin().getId());



        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }

    @Test
    @Rollback(value = false)
    public void approve() {
        Admin admin = adminService.get(1L);

        //Can't approve order. No such order in DB
        assertNull("Approve order, that Not exist in DB",
                orderService.approve(-1L, admin));

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);

        order.setAdmin(admin);

        User user = userService.get(1L);
        order.setUser(user);

        Room room = roomDao.getAnyForRoomType(1L);
        order.setRoom(room);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        order.setBill(null);

        // save Order in Db,
        Order addOrder = orderService.add(order);

        // try approve Expired order
        assertNull("approve Expired order",
                orderService.approve(addOrder.getId(), addOrder.getAdmin()));

        // set to addOrder correct ArrivalDate and  EventsDate
        Date currentDate = new Date((new java.util.Date()).getTime());
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        addOrder.setArrivalDate(Date.valueOf(tomorrow));
        addOrder.setEventsDate(Date.valueOf(dayAfterTomorrow));

        //update addOrder
        addOrder = orderService.update(addOrder);

        // approve addOrder
        Admin adminWhoApproveOrder = adminService.get(2L);
        Bill bill = orderService.approve(addOrder.getId(), adminWhoApproveOrder);

        // check Correctness of approval
        assertNotNull("Approved order make null bill", bill);
        assertEquals("Bill corresponding to the order has status NOT BillStatusType.UNPAID",
                BillStatusType.UNPAID, bill.getStatus());
        assertEquals("approved Order has status defer to OrderStatusType.APPROVED",
                OrderStatusType.APPROVED,addOrder.getStatus());
        assertEquals("approved Order refer on incorrect bill", bill, addOrder.getBill());
        assertEquals("Order has wrong Admin", adminWhoApproveOrder, order.getAdmin());


        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(bill.getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }

    @Test
    public void getAllForUserId() {
        // returns an empty list because the user did not make any orders
        assertTrue("There are orders made by user with id [-1L]",
                orderService.getAllForUserId(-1L).isEmpty());

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);

        Admin admin = adminService.get(1L);
        order.setAdmin(admin);

        User user = userService.get(1L);
        order.setUser(user);

        Room room = roomDao.getAnyForRoomType(1L);
        order.setRoom(room);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        order.setBill(null);

        // take List of Orders made by user before Save new one order
        List<Order> beforeSaveList = orderService.getAllForUserId(user.getId());

        // save Order in Db,
        Order addOrder = orderService.add(order);

        // take List of Orders made by user after Save new one order
        List<Order> afterSaveList = orderService.getAllForUserId(user.getId());

        assertEquals("afterSaveList size minus beforeSaveList size not equals [1]",
                1, afterSaveList.size() - beforeSaveList.size());


        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());

        assertNull("Order after deletion is NOT null", getItOrder);
    }

    @Test
    public void getAllForStatusAndUser() {
        // returns an empty list because the user did not make any orders
        assertTrue("There are orders made by user with id [-1L]",
                orderService.getAllForStatusAndUser(OrderStatusType.APPROVED,-1L).isEmpty());

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);

        Admin admin = adminService.get(1L);
        order.setAdmin(admin);

        User user = userService.get(1L);
        order.setUser(user);

        Room room = roomDao.getAnyForRoomType(1L);
        order.setRoom(room);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        order.setBill(null);

        // take List of Orders made by user before Save new one order
        List<Order> beforeSaveList = orderService
                .getAllForStatusAndUser(OrderStatusType.NEW, user.getId());

        // save Order in Db,
        Order addOrder = orderService.add(order);

        // take List of Orders made by user after Save new one order
        List<Order> afterSaveList = orderService
                .getAllForStatusAndUser(OrderStatusType.NEW, user.getId());

        assertEquals("afterSaveList size minus beforeSaveList size not equals [1]",
                1, afterSaveList.size() - beforeSaveList.size());


        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());

        assertNull("Order after deletion is NOT null", getItOrder);
    }

    @Test
    public void getAllForStatusAndHotel() {
        // returns an empty list because the user did not make any orders
        assertTrue("There are orders made by user in Hotel with id [-1L]",
                orderService.getAllForStatusAndHotel(OrderStatusType.APPROVED,-1L).isEmpty());

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);

        Admin admin = adminService.get(1L);
        order.setAdmin(admin);

        User user = userService.get(1L);
        order.setUser(user);

        Room room = roomDao.getAnyForRoomType(1L);
        order.setRoom(room);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        order.setBill(null);

        // take List of Orders made by user before Save new one order
        List<Order> beforeSaveList = orderService
                .getAllForStatusAndHotel(OrderStatusType.NEW, admin.getHotel().getId());

        // save Order in Db,
        Order addOrder = orderService.add(order);

        // take List of Orders made by user after Save new one order
        List<Order> afterSaveList = orderService
                .getAllForStatusAndHotel(OrderStatusType.NEW, admin.getHotel().getId());

        assertEquals("afterSaveList size minus beforeSaveList size not equals [1]",
                1, afterSaveList.size() - beforeSaveList.size());


        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());

        assertNull("Order after deletion is NOT null", getItOrder);
    }
}
