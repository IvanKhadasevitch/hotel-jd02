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
import services.*;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional
public class BillServiceTest {

    @Autowired
    IBillService billService;

    @Autowired
    IOrderService orderService;

    @Autowired
    IAdminService adminService;

    @Autowired
    IUserService userService;

    @Autowired
    IRoomDao roomDao;

    @Test
    @Rollback(value = true)
    public void billPay() {
        // no such bill in DB -> can't pay
        assertFalse("There are paid bill with id [-1]", billService.billPay(-1L));

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminService.get(1L);
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

        // save Order and Bill in Db,
        Order addOrder = orderService.add(order);
        Bill addBill = billService.add(bill);

        // bill already paid -> can't pay
        assertFalse("bill already paid has status not [BillStatusType.PAID]",
                billService.billPay(addBill.getId()));

        // change Bill status to UnPaid
        addBill.setStatus(BillStatusType.UNPAID);
        Bill unpaidBill =  billService.update(addBill);
        unpaidBill = billService.get(unpaidBill.getId());

        assertEquals("Unpaid bill has status not [BillStatusType.UNPAID]",
                BillStatusType.UNPAID, unpaidBill.getStatus());

        // pay unpaidBill
        assertTrue("Can't pay bill with status [BillStatusType.UNPAID]",
                billService.billPay(unpaidBill.getId()));

        // check correctness of bill payment
        assertTrue("paid Bill don't has status [BillStatusType.PAID]",
                BillStatusType.PAID.equals(unpaidBill.getStatus()));
        assertTrue("Order corresponded to the paid Bill don't has status [OrderStatusType.PAID]",
                OrderStatusType.PAID.equals(addOrder.getStatus()));


        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }

    @Test
    @Rollback(value = true)
    public void getAllForUserId() {
        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminService.get(1L);
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

        List<Bill> beforeSaveList = billService.getAllForUserId(user.getId());

        // save Order and Bill in Db,
        Order addOrder = orderService.add(order);
        Bill addBill = billService.add(bill);

        List<Bill> afterSaveList = billService.getAllForUserId(user.getId());

        assertEquals("List size after safe Bill minus List size before safe " +
                "not equals [1]", 1, afterSaveList.size() - beforeSaveList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndUser() {
        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminService.get(1L);
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

        List<Bill> beforeSaveList = billService.getAllForStatusAndUser(bill.getStatus(),
                user.getId());

        // save Order and Bill in Db,
        Order addOrder = orderService.add(order);
        Bill addBill = billService.add(bill);

        List<Bill> afterSaveList = billService.getAllForStatusAndUser(addBill.getStatus(),
                user.getId());

        assertEquals("List size after safe Bill minus List size before safe " +
                "not equals [1]", 1, afterSaveList.size() - beforeSaveList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndHotel() {
        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminService.get(1L);
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

        List<Bill> beforeSaveList = billService.getAllForStatusAndHotel(bill.getStatus(),
                admin.getHotel().getId());

        // save Order and Bill in Db,
        Order addOrder = orderService.add(order);
        Bill addBill = billService.add(bill);


        List<Bill> afterSaveList = billService.getAllForStatusAndHotel(addBill.getStatus(),
                admin.getHotel().getId());

        assertEquals("List size after safe Bill minus List size before safe " +
                "not equals [1]", 1, afterSaveList.size() - beforeSaveList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());
        Bill getItBill = billService.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);
    }
}
