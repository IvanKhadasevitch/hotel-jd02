package dao.impl;

import dao.*;
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

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")

public class BillDaoTest {
    @Autowired
    private BaseDao<Bill> baseDao;

    @Autowired
    private IBillDao billDao;
    @Autowired
    private IOrderDao orderDao;
    @Autowired
    private IAdminDao adminDao;
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IRoomDao roomDao;
    @Test
    @Rollback(value = true)
    public void getAllForUserId() {
        // make Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // make Bill
        Bill bill = new Bill();
        bill.setOrder(order);
        order.setBill(bill);

        bill.setUser(user);
        Room room = roomDao.getAnyForRoomType(1L);
        bill.setRoom(room);

        bill.setArrivalDate(order.getArrivalDate());
        bill.setEventsDate(order.getEventsDate());
        bill.setStatus(BillStatusType.UNPAID);

        // take List<Bill> before save Bill
        List<Bill> beforeList = billDao.getAllForUserId(user.getId());

        // save Order and Bill in Db,
        Order addOrder = orderDao.add(order);
        Bill addBill = billDao.add(bill);

        //take List<Bill> after save Bill
        List<Bill> afterList = billDao.getAllForUserId(user.getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe = [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderDao.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderDao.get(addOrder.getId());
        Bill getItBill = billDao.get(addOrder.getBill().getId());

        assertNull("Order after deletion is null", getItOrder);
        assertNull("Bill after deletion is null", getItBill);

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndUser() {
        // make Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // make Bill
        Bill bill = new Bill();
        bill.setOrder(order);
        order.setBill(bill);

        bill.setUser(user);
        Room room = roomDao.getAnyForRoomType(1L);
        bill.setRoom(room);
        bill.setArrivalDate(order.getArrivalDate());
        bill.setEventsDate(order.getEventsDate());
        bill.setStatus(BillStatusType.UNPAID);


        // take List<Bill> before save Bill
        List<Bill> beforeList = billDao.getAllForStatusAndUser(bill.getStatus(), user.getId());

        // save Order and Bill in Db,
        Order addOrder = orderDao.add(order);
        Bill addBill = billDao.add(bill);

        //take List<Bill> after save Bill
        List<Bill> afterList = billDao.getAllForStatusAndUser(bill.getStatus(), user.getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe = [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderDao.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderDao.get(addOrder.getId());
        Bill getItBill = billDao.get(addOrder.getBill().getId());

        assertNull("Order after deletion is null", getItOrder);
        assertNull("Bill after deletion is null", getItBill);

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndHotel() {
        // make Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // make Bill
        Bill bill = new Bill();
        bill.setOrder(order);
        order.setBill(bill);

        bill.setUser(user);
        Room room = roomDao.getAnyForRoomType(1L);
        bill.setRoom(room);
        bill.setArrivalDate(order.getArrivalDate());
        bill.setEventsDate(order.getEventsDate());
        bill.setStatus(BillStatusType.UNPAID);

        // take List<Bill> before save Bill
        List<Bill> beforeList = billDao.getAllForStatusAndHotel(bill.getStatus(),
                admin.getHotel().getId());

        // save Order and Bill in Db,
        Order addOrder = orderDao.add(order);
        Bill addBill = billDao.add(bill);

        //take List<Bill> after save Bill
        List<Bill> afterList = billDao.getAllForStatusAndHotel(bill.getStatus(),
                admin.getHotel().getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe NOT = [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderDao.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderDao.get(addOrder.getId());
        Bill getItBill = billDao.get(addOrder.getBill().getId());

        assertNull("Order after deletion is NOT null", getItOrder);
        assertNull("Bill after deletion is NOT null", getItBill);

        baseDao.getEm().clear();
    }
}
