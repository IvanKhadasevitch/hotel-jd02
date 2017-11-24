package dao.impl;

import dao.IOrderDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.Order;
import pojos.enums.OrderStatusType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Repository
public class OrderDao extends BaseDao<Order> implements IOrderDao {
    private static Logger log = Logger.getLogger(OrderDao.class);

    @Autowired
    public OrderDao() {
        super();
        clazz = Order.class;
    }

    @Override
    public List<Order> getAllForUserId(Serializable userId) {
        CriteriaQuery<Order> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Order> root = (Root<Order>) criteria.getRoots().toArray()[0];

        criteria.where(cb.equal(root.get("user").get("id"), userId));

        List<Order> resultList = getListWhere(criteria);
        log.info("For User with id: [" + userId + "]. Get all Orders: " + resultList);

        return resultList;
    }

    @Override
    public List<Order> getAllForStatusAndUser(OrderStatusType orderStatus, Serializable userId) {
        CriteriaQuery<Order> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Order> root = (Root<Order>) criteria.getRoots().toArray()[0];

        Predicate predicateUserId = cb.equal(root.get("user").get("id"), userId);
        Predicate predicateOrderStatus = cb.equal(root.get("status"), orderStatus);
        criteria.where(cb.and(predicateUserId, predicateOrderStatus));

        List<Order> resultList = getListWhere(criteria);
        log.info("For User with id: [" + userId + "]. Get all Orders with status [" +
                orderStatus + "]: " + resultList);

        return resultList;
    }

    @Override
    public List<Order> getAllForStatusAndHotel(OrderStatusType orderStatus, Serializable hotelId) {
        CriteriaQuery<Order> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Order> root = (Root<Order>) criteria.getRoots().toArray()[0];

        Predicate predicateHotelId = cb.equal(root.get("room")
                                                  .get("roomType")
                                                  .get("hotel").get("id"), hotelId);
        Predicate predicateOrderStatus = cb.equal(root.get("status"), orderStatus);
        criteria.where(cb.and(predicateHotelId, predicateOrderStatus));

        List<Order> resultList = getListWhere(criteria);
        log.info("For Hotel with id: [" + hotelId + "]. Get all Orders with status [" +
                orderStatus + "]: " + resultList);

        return resultList;
    }

//  private static final String GET_ALL_BOOKED_SQL =
//   "SELECT * FROM `order` WHERE (order_status=OrderStatusType.APPROVED OR order_status=OrderStatusType.PAID) " +
//                    "AND order_roomType_id=roomTypeId AND ( " +
//                    "(order_arrivalDate <= arrivalDate AND arrivalDate < order_eventsDate) or " +
//                    "(order_arrivalDate < eventsDate AND eventsDate <= order_eventsDate) or " +
//                    "(arrivalDate <= order_arrivalDate AND order_eventsDate <= eventsDate) or " +
//                    "(order_arrivalDate <= arrivalDate AND eventsDate <= order_eventsDate)" +
//                    ") ";

    @Override
    public List<Order> getAllBooked(Serializable roomTypeId, Date arrivalDate, Date eventsDate) {
        CriteriaQuery<Order> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Order> root = (Root<Order>) criteria.getRoots().toArray()[0];

        Predicate predicateOrderStatus = cb.or(cb.equal(root.get("status"), OrderStatusType.APPROVED),
                                               cb.equal(root.get("status"), OrderStatusType.PAID));
        Predicate predicateRoomType = cb.equal(root.get("room").get("roomType").get("id"), roomTypeId);

        Predicate tailInInterval = cb.and(cb.lessThanOrEqualTo(root.get("arrivalDate"), arrivalDate),
                                          cb.greaterThan(root.get("eventsDate"), arrivalDate));
        Predicate headInInterval = cb.and(cb.lessThan(root.get("arrivalDate"), eventsDate),
                                          cb.greaterThanOrEqualTo(root.get("eventsDate"), eventsDate));
        Predicate tailAndHeadOutsideInterval = cb.and(cb.greaterThanOrEqualTo(root.get("arrivalDate"), arrivalDate),
                                               cb.lessThanOrEqualTo(root.get("eventsDate"), eventsDate));
        Predicate tailAndHeadInsideInterval = cb.and(cb.lessThanOrEqualTo(root.get("arrivalDate"), arrivalDate),
                                              cb.greaterThanOrEqualTo(root.get("eventsDate"), eventsDate));


        Predicate tailAndHeadVariants = cb.or(tailInInterval, headInInterval,
                                              tailAndHeadOutsideInterval, tailAndHeadInsideInterval);

        criteria.where(cb.and(predicateOrderStatus, predicateRoomType, tailAndHeadVariants));

        List<Order> resultList = getListWhere(criteria);
        log.info("For RoomType with id: [" + roomTypeId + "]. Get all Booked for arrivalDate [" +
                arrivalDate + "] and eventsDate [" + eventsDate + "], orders: " + resultList);

        return resultList;
    }
}
