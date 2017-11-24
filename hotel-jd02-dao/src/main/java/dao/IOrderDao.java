package dao;


import pojos.Order;
import pojos.enums.OrderStatusType;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public interface IOrderDao extends IDao<Order> {
    /**
     * return all Orders for User with id=userId,
     * if not one found - return empty List
     */
    List<Order> getAllForUserId(Serializable userId);

    /**
     * return all Orders for User with id=userId and OrderStatusType=orderStatus,
     * if not one found - return empty List
     */
    List<Order> getAllForStatusAndUser(OrderStatusType orderStatus, Serializable userId);

    /**
     * return all Orders for Hotel with id=hotelId and OrderStatusType=orderStatus,
     * if not one found - return empty List
     */
    List<Order> getAllForStatusAndHotel(OrderStatusType orderStatus, Serializable hotelId);

    /**
     * return all Orders that are booking rooms for Room type with id=roomTypeId within date interval
     * arrival Date=orderStatus and events Date=eventsDate,
     * if not one found - return empty List
     */
    List<Order> getAllBooked(Serializable roomTypeId, Date arrivalDate, Date eventsDate);
}
