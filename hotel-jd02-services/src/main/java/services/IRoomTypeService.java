package services;


import pojos.RoomType;

import java.io.Serializable;
import java.util.List;

public interface IRoomTypeService extends IService<RoomType> {

    /**
     *
     * @param hotelId determine Hotel id where are rooms with room type
     * @return a list of all room types belonging to the hotel.
     *
     * returns an empty list if there are no room types belonging to the hotel
     */
    List<RoomType> getAllForHotel(Serializable hotelId);
}
