package services;


import pojos.Room;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public interface IRoomService extends IService<Room> {

    /**
     *
     * @param roomTypeId determines the room type id that the guest booked
     * @param arrivalDate determine gust arrive Date
     * @param eventsDate determine gust events Date
     * @return returns a list of available rooms whose type is given by roomType
     * with Id=roomTypeId within period determined by arrivalDate and eventsDate.
     *
     * returns an empty list if there are no available rooms of the type specified by roomType
     * with Id=roomTypeId within specified period.
     */
    List<Room> getAllFreeForRoomType(Serializable roomTypeId, Date arrivalDate, Date eventsDate);
}
