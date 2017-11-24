package dao.impl;

import dao.IRoomTypeDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.RoomType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Repository
public class RoomTypeDao extends BaseDao<RoomType> implements IRoomTypeDao {
    private static Logger log = Logger.getLogger(RoomTypeDao.class);

    @Autowired
    public RoomTypeDao() {
        super();
        clazz = RoomType.class;
    }

    @Override
    public List<RoomType> getAllForHotel(Serializable hotelId) {
        CriteriaQuery<RoomType> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<RoomType> root = (Root<RoomType>) criteria.getRoots().toArray()[0];

        criteria.where(cb.equal(root.get("hotel").get("id"), hotelId));

        List<RoomType> resultList = getListWhere(criteria);
        log.info("For Hotel with id: [" + hotelId + "]. Get all RoomType: " + resultList);

        return resultList;
    }
}
