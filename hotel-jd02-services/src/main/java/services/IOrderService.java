package services;


import pojos.Admin;
import pojos.Bill;
import pojos.Order;
import pojos.enums.OrderStatusType;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public interface IOrderService extends IService<Order> {
    /**
     *
     * @param roomTypeId determines the room type id that the guest booked
     * @param arrivalDate determine gust arrive Date
     * @param eventsDate determine gust events Date
     * @return new Order where room set as eny room where roomType with id=roomTypeId,
     * arrive Date as arrivalDate, events Date as eventsDate, order status as OrderStatusType.NEW,
     * total calculated as multiple of price for the night and period of days
     * within arrivalDate and eventsDate.
     */
    Order make(Serializable roomTypeId, Date arrivalDate, Date eventsDate);

    /**
     *
     * @param orderId determines the id of the order that should be decline
     * @param admin determines Administrator who will decline Order
     * @return true if order successfully decline,
     * return false if No such order in DB or if try decline order with status
     * defer from OrderStatusType.NEW, or admin=null
     */
    boolean decline(Serializable orderId, Admin admin);

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
    Bill approve(Serializable orderId,  Admin admin);

    /**
     *
     * @param userId determines the id of the User, who make Order
     * @return the list of all Orders made by the user,
     *
     * returns an empty list if the user did not make any orders
     */
    List<Order> getAllForUserId(Serializable userId);

    /**
     *
     * @param orderStatus determine Order status
     * @param userId determines the id of the User, who make Order
     * @return the list of all Orders with status=orderStatus made by the user,
     *
     * returns an empty list if the user did not make any orders with status=orderStatus
     */
    List<Order> getAllForStatusAndUser(OrderStatusType orderStatus, Serializable userId);

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
    List<Order> getAllForStatusAndHotel(OrderStatusType orderStatus, Serializable hotelId);
}
