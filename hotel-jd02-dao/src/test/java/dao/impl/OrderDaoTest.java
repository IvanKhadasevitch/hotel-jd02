package dao.impl;

import dao.IAdminDao;
import dao.IOrderDao;
import dao.IRoomDao;
import dao.IUserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.*;
import pojos.enums.OrderStatusType;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class OrderDaoTest {
    @Autowired
    private BaseDao<Order> baseDao;

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
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // take List<Order> before save Order
        List<Order> beforeList = orderDao.getAllForUserId(user.getId());

        // save Order in Db
        Order add = orderDao.add(order);

        //take List<Order> after save Order
        List<Order> afterList = orderDao.getAllForUserId(user.getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe = [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB
        orderDao.delete(add.getId());

        // check Correctness of removal
        Order getIt = orderDao.get(add.getId());

        assertNull("Entity after deletion is null", getIt);

        baseDao.getEm().clear();

    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndUser() {
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // take List<Order> before save Order
        List<Order> beforeList = orderDao.getAllForStatusAndUser(OrderStatusType.APPROVED,
                user.getId());

        // save Order in Db
        Order add = orderDao.add(order);

        //take List<Order> after save Order
        List<Order> afterList = orderDao.getAllForStatusAndUser(OrderStatusType.APPROVED,
                user.getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe = [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB
        orderDao.delete(add.getId());

        // check Correctness of removal
        Order getIt = orderDao.get(add.getId());

        assertNull("Entity after deletion is null", getIt);

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void getAllForStatusAndHotel() {
        Order order = new Order();
        order.setStatus(OrderStatusType.APPROVED);

        Admin admin = adminDao.get(1L);
        order.setAdmin(admin);

        User user = userDao.get(1L);
        order.setUser(user);
        Room room = roomDao.getAnyForRoomType(1L);
        order.setRoom(room);

        order.setArrivalDate(Date.valueOf("1970-01-01"));
        order.setEventsDate(Date.valueOf("1970-01-11"));

        // take List<Order> before save Order
        List<Order> beforeList = orderDao.getAllForStatusAndHotel(OrderStatusType.APPROVED,
                admin.getHotel().getId());

        // save Order in Db
        Order add = orderDao.add(order);

        //take List<Order> after save Order
        List<Order> afterList = orderDao.getAllForStatusAndHotel(OrderStatusType.APPROVED,
                admin.getHotel().getId());

        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe != [1]",
                1, afterList.size() - beforeList.size());

        // delete saved Order from DB
        orderDao.delete(add.getId());

        // check Correctness of removal
        Order getIt = orderDao.get(add.getId());

        assertNull("Entity after deletion is null", getIt);

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void getAllBooked() {
        Order order1 = new Order();

        Admin admin = adminDao.get(1L);
        order1.setAdmin(admin);

        User user = userDao.get(1L);
        order1.setUser(user);

        Room room = roomDao.get(1L);
        order1.setRoom(room);

        // take List<Order> before save Order
        List<Order> beforeList = orderDao.getAllBooked(room.getRoomType().getId(),
                        Date.valueOf("1980-01-01"), Date.valueOf("1980-01-10"));

        // save Orders in Db
        order1.setStatus(OrderStatusType.NEW);
        order1.setArrivalDate(Date.valueOf("1980-01-01"));
        order1.setEventsDate(Date.valueOf("1980-01-10"));
        orderDao.add(order1);
        // save entity id
        List<Long> entityIdList = new LinkedList<>();
        entityIdList.add(order1.getId());
        // detach entity and entity.id and entity.version set in null
        baseDao.getEm().detach(order1);
        order1.setId(null);
        order1.setVersion(null);

        order1.setStatus(OrderStatusType.APPROVED);
        order1.setArrivalDate(Date.valueOf("1980-01-01"));
        order1.setEventsDate(Date.valueOf("1980-01-12"));
        orderDao.add(order1);
        // save entity id
        entityIdList.add(order1.getId());
        // detach entity and entity.id and entity.version set in null
        baseDao.getEm().detach(order1);
        order1.setId(null);
        order1.setVersion(null);

        order1.setStatus(OrderStatusType.APPROVED);
        order1.setArrivalDate(Date.valueOf("1979-01-01"));
        order1.setEventsDate(Date.valueOf("1980-01-10"));
        orderDao.add(order1);
        // save entity id
        entityIdList.add(order1.getId());
        // detach entity and entity.id and entity.version set in null
        baseDao.getEm().detach(order1);
        order1.setId(null);
        order1.setVersion(null);

        order1.setStatus(OrderStatusType.PAID);
        order1.setArrivalDate(Date.valueOf("1980-01-03"));
        order1.setEventsDate(Date.valueOf("1980-01-10"));
        orderDao.add(order1);
        // save entity id
        entityIdList.add(order1.getId());
        // detach entity;  entity.id and entity.version set in null
        baseDao.getEm().detach(order1);
        order1.setId(null);
        order1.setVersion(null);

        order1.setStatus(OrderStatusType.APPROVED);
        order1.setArrivalDate(Date.valueOf("1979-01-01"));
        order1.setEventsDate(Date.valueOf("1980-01-12"));
        orderDao.add(order1);
        // save entity id
        entityIdList.add(order1.getId());
        // detach entity;  entity.id and entity.version set in null
        baseDao.getEm().detach(order1);
        order1.setId(null);
        order1.setVersion(null);

        //take List<Order> after save Order
        List<Order> afterList = orderDao.getAllBooked(room.getRoomType().getId(),
                Date.valueOf("1980-01-01"), Date.valueOf("1980-01-10"));


        // check correct List size
        assertNotNull(beforeList);
        assertNotNull(afterList);
        assertEquals("List.size after safe minus List.size before safe = [4]",
                4, afterList.size() - beforeList.size());

        // delete saved Orders from DB
        int countDeletedId = 0;
        for (Long id : entityIdList) {
            Order order = orderDao.get(id);
            if (order != null) {
                orderDao.delete(order.getId());
                countDeletedId++;
            }
            // check Correctness of removal
            Order getIt = orderDao.get(id);
            assertNull("Entity with id [" + id + "] after deletion is null", getIt);
        }
        // check quantities of removed entities
        assertEquals("Delete [" + entityIdList.size() + "] entities.",
                entityIdList.size(), countDeletedId);

        baseDao.getEm().clear();
    }
}
