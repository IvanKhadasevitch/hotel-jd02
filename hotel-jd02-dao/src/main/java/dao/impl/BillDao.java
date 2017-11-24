package dao.impl;

import dao.IBillDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.Bill;
import pojos.enums.BillStatusType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Repository
public class BillDao extends BaseDao<Bill> implements IBillDao {
    private static Logger log = Logger.getLogger(BillDao.class);

    @Autowired
    public BillDao() {
        super();
        clazz = Bill.class;
    }

    @Override
    public List<Bill> getAllForUserId(Serializable userId) {
        CriteriaQuery<Bill> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Bill> root = (Root<Bill>) criteria.getRoots().toArray()[0];

        criteria.where(cb.equal(root.get("user").get("id"), userId));

        List<Bill> resultList = getListWhere(criteria);
        log.info("For User with id: [" + userId + "]. Get all Bills: " + resultList);

        return resultList;
    }

    @Override
    public List<Bill> getAllForStatusAndUser(BillStatusType billStatus, Serializable userId) {
        CriteriaQuery<Bill> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Bill> root = (Root<Bill>) criteria.getRoots().toArray()[0];

        Predicate predicateUserId = cb.equal(root.get("user")
                                                 .get("id"), userId);
        Predicate predicateBillStatus = cb.equal(root.get("status"), billStatus);
        criteria.where(cb.and(predicateUserId, predicateBillStatus));

        List<Bill> resultList = getListWhere(criteria);
        log.info("For User with id: [" + userId + "]. Get all Bills with status [" +
                billStatus + "], bills: " + resultList);

        return resultList;
    }

    @Override
    public List<Bill> getAllForStatusAndHotel(BillStatusType billStatus, Serializable hotelId) {
        CriteriaQuery<Bill> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Bill> root = (Root<Bill>) criteria.getRoots().toArray()[0];

        Predicate predicateHotelId = cb.equal(root.get("room")
                                                  .get("roomType")
                                                  .get("hotel").get("id"), hotelId);
        Predicate predicateOrderStatus = cb.equal(root.get("status"), billStatus);
        criteria.where(cb.and(predicateHotelId, predicateOrderStatus));

        List<Bill> resultList = getListWhere(criteria);
        log.info("For Hotel with id: [" + hotelId + "]. Get all Bills with status [" +
                billStatus + "] bills: " + resultList);

        return resultList;
    }
}
