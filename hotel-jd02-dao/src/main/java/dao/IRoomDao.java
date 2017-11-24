package dao;


import pojos.Room;

import java.io.Serializable;
import java.util.List;

public interface IRoomDao extends IDao<Room> {
    // get all rooms for RoomType with id = roomTypeId
    List<Room> getAllForRoomType(Serializable roomTypeId);

    // return null if not one Room exist for RoomType
    Room getAnyForRoomType(Serializable roomTypeId);
}
