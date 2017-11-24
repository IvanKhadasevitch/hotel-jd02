package dao;


import pojos.RoomType;

import java.io.Serializable;
import java.util.List;

public interface IRoomTypeDao extends IDao<RoomType> {
    /**
     * return all RoomTypes for Hotel with id = hotelId
     * if not one found - return empty List
     */
    List<RoomType> getAllForHotel(Serializable hotelId);
}
