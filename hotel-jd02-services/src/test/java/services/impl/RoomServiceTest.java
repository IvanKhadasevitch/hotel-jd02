package services.impl;

import dao.IRoomDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.Admin;
import pojos.Order;
import pojos.Room;
import pojos.User;
import pojos.enums.OrderStatusType;
import services.IAdminService;
import services.IOrderService;
import services.IRoomService;
import services.IUserService;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional
public class RoomServiceTest {
    @Autowired
    IRoomService roomService;

    @Autowired
    IAdminService adminService;

    @Autowired
    IUserService userService;

    @Autowired
    IOrderService orderService;

    @Autowired
    IRoomDao roomDao;

    @Test
    @Rollback(value = false)
    public void getAllFreeForRoomType() {
        // All room free with room Type id=1L
        List<Room> freeRooms = roomService.getAllFreeForRoomType(1L,
                Date.valueOf("1970-01-01"), Date.valueOf("1970-01-02"));
        assertEquals("Not All room free with room Type id=1L ", 10, freeRooms.size());

        // make new Order
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminService.get(1L);
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

        // One room booked, aether rooms free with room Type id=1L
        freeRooms = roomService.getAllFreeForRoomType(1L,
                Date.valueOf("1970-01-01"), Date.valueOf("1970-01-02"));

        assertEquals("Not only one room booked with room Type id=1L (10-1=9) ",
                9, freeRooms.size());

        // delete saved Order from DB; Bill will be deleted too by cascade
        orderService.delete(addOrder.getId());

        // check Correctness of removal
        Order getItOrder = orderService.get(addOrder.getId());

        assertNull("Order after deletion is NOT null", getItOrder);
    }
}
