package services.impl;

import dao.IBillDao;
import dao.IOrderDao;
import dao.IRoomDao;
import dao.IRoomTypeDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojos.*;
import pojos.enums.BillStatusType;
import pojos.enums.OrderStatusType;
import services.IOrderService;
import services.IRoomService;

import java.io.Serializable;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(transactionManager = "txManager")
public class OrderService extends BaseService<Order> implements IOrderService {
    private static Logger log = Logger.getLogger(OrderService.class);

    @Autowired
    private IRoomDao roomDao;

    @Autowired
    private IOrderDao orderDao;

    @Autowired
    private IBillDao billDao;

    @Autowired
    private IRoomService roomService;

    public OrderService() {
        super();
    }

    /**
     * @param roomTypeId determines the room type id that the guest booked
     * @param arrivalDate determine gust arrive Date
     * @param eventsDate determine gust events Date
     * @return new Order where room set as eny room where roomType with id=roomTypeId,
     * arrive Date as arrivalDate, events Date as eventsDate, order status as OrderStatusType.NEW,
     * total calculated as multiple of price for the night and period of days
     * within arrivalDate and eventsDate.
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Order make(Serializable roomTypeId, Date arrivalDate, Date eventsDate) {
        Order order = new Order();
        order.setStatus(OrderStatusType.NEW);
        Room room = roomDao.getAnyForRoomType(roomTypeId);
        order.setRoom(room);
        order.setArrivalDate(arrivalDate);
        order.setEventsDate(eventsDate);
        // calculate order.total
        Integer price = room.getRoomType().getPrice();
        long periodDays = order.getArrivalDate().toLocalDate()
                               .until(order.getEventsDate().toLocalDate(), ChronoUnit.DAYS);
        order.setTotal(price * periodDays);

        return order;
    }

    /**
     *
     * @param orderId determines the id of the order that should be decline
     * @param admin determines Administrator who will decline Order
     * @return true if order successfully decline.
     * 
     * return false if No such order in DB or if try decline order with status
     * defer from OrderStatusType.NEW, or admin=null
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean decline(Serializable orderId, Admin admin) {
        // not define admin
        if (admin == null) {
            log.info("Can't decline order with id [" + orderId + "]. Admin=null");
            return false;
        }

        Order order = orderDao.get(orderId);
        // if No Order with orderId in DB
        if (order == null) {
            log.info("Can't decline order with id [" + orderId + "]. No such order in DB");
            return false;
        }

        // check what order NEW, Can't decline not NEW order
        if ( ! OrderStatusType.NEW.equals(order.getStatus())) {
            log.info("Can't decline order with id [" + orderId +
                    "].Order not NEW. Order has status: " + order.getStatus());
            return false;
        }

        // decline order with status NEW
        order.setAdmin(admin);
        order.setStatus(OrderStatusType.DECLINE);
        orderDao.update(order);

        log.info("Order: " + order + " is decline: " + true);

        return true;
    }

    /**
     *
     * @param orderId determines the id of the order that should be decline
     * @param admin determines Administrator who will approve Order
     * @return the Bill with status BillStatusType.UNPAID that should be paid
     * to the guest after the successful approval of the order by the administrator (Admin).
     * Change order status to OrderStatusType.APPROVED. Determine free room for order.
     *
     * return null if No such order with id=orderId in DB or Expired order
     * or No free rooms for order, or admin=null, or order already approved
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Bill approve(Serializable orderId,  Admin admin) {
        // not define admin
        if (admin == null) {
            log.info("Can't approve order with id [" + orderId + "]. Admin=null");
            return null;
        }


        Order order = orderDao.get(orderId);
        //order deleted of DB -> return bill=null
        if (order == null) {
            log.info("Can't approve order with id [" + orderId + "]. No such order in DB");
            return null;
        }

        // check what order is NEW order, Can't approve not NEW order
        if ( ! OrderStatusType.NEW.equals(order.getStatus()) ) {
            log.info("Can't approve order with id [" + orderId +
                    "].Not NEW order. Order status: " + order.getStatus());
            return null;
        }

        // check expired Order
        Date currentDate = new Date((new java.util.Date()).getTime());
        boolean isExpired = currentDate.after(order.getArrivalDate());

        // expired Order can't approve -> return bill=null
        if (isExpired) {
            log.info("Can't approve order with id [" + orderId + "]. Expired order: " + order);
            return null;
        }

        // take free room
        List<Room> freeRoomsList = roomService
                .getAllFreeForRoomType(order.getRoom().getRoomType().getId(),
                                       order.getArrivalDate(), order.getEventsDate());
        // no free rooms
        if (freeRoomsList.isEmpty()) {
            log.info("Can't approve order with id [" + orderId + "]. No free rooms for order: " +
                    order);
            return null;
        }

        //take first free room from list
        Room freeRoom = freeRoomsList.get(0);

        // make bill
        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setUser(order.getUser());
        bill.setRoom(freeRoom);
        bill.setArrivalDate(order.getArrivalDate());
        bill.setEventsDate(order.getEventsDate());
        bill.setTotal(order.getTotal());
        bill.setStatus(BillStatusType.UNPAID);

        // save bill in DB
        bill = billDao.add(bill);
        // update order; set order APPROVED
        order.setAdmin(admin);
        order.setStatus(OrderStatusType.APPROVED);
        order.setRoom(freeRoom);
        order.setBill(bill);
        orderDao.update(order);

        log.info("Approve order with id [" + orderId + "]. Approved order: " + order);
        return bill;
    }

    /**
     *
     * @param userId determines the id of the User, who make Order
     * @return the list of all Orders made by the user,
     *
     * returns an empty list if the user did not make any orders
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Order> getAllForUserId(Serializable userId) {
        return orderDao.getAllForUserId(userId);
    }

    /**
     *
     * @param orderStatus determine Order status
     * @param userId determines the id of the User, who make Order
     * @return the list of all Orders with status=orderStatus made by the user,
     *
     * returns an empty list if the user did not make any orders with status=orderStatus
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Order> getAllForStatusAndUser(OrderStatusType orderStatus, Serializable userId) {
        return orderDao.getAllForStatusAndUser(orderStatus, userId);
    }

    /**
     *
     * @param orderStatus determine Order status
     * @param hotelId determine Hotel id where user made oder
     * @return the list of all Orders with status=orderStatus and Hotel id=hotelId
     * made by the user,
     *
     * returns an empty list if the user did not make any orders with
     * status=orderStatus and Hotel id=hotelId
     */
    @Override
    public List<Order> getAllForStatusAndHotel(OrderStatusType orderStatus, Serializable hotelId) {
        return orderDao.getAllForStatusAndHotel(orderStatus, hotelId);
    }
}
