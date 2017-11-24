package dao.impl;

import dao.IRoomDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.Room;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Repository
public class RoomDao extends BaseDao<Room> implements IRoomDao {
    private static Logger log = Logger.getLogger(RoomDao.class);

    @Autowired
    public RoomDao() {
        super();
        clazz = Room.class;
    }

    @Override
    public List<Room> getAllForRoomType(Serializable roomTypeId) {
        CriteriaQuery<Room> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<Room> root = (Root<Room>) criteria.getRoots().toArray()[0];

        criteria.where(cb.equal(root.get("roomType").get("id"), roomTypeId));

        List<Room> resultList = getListWhere(criteria);
        log.info("For RoomType with id: [" + roomTypeId + "]. Get all Rooms: " + resultList);

        return resultList;
    }

    @Override
    public Room getAnyForRoomType(Serializable roomTypeId) {
        List<Room> roomList = getAllForRoomType(roomTypeId);
        if (roomList.isEmpty()) {
            log.info("In DB no any room for roomType with id: " + roomTypeId);
            return null;
        } else {
            log.info(String.format("Get room for roomType with id [%s], room: ",
                    roomTypeId, roomList.get(0) ));
            return roomList.get(0);
        }


    }
}
