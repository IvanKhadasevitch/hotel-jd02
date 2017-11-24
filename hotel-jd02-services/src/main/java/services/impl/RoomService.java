package services.impl;

import dao.IDao;
import dao.IOrderDao;
import dao.IRoomDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Order;
import pojos.Room;
import services.IRoomService;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(transactionManager = "txManager")
public class RoomService extends BaseService<Room> implements IRoomService {
    private static Logger log = Logger.getLogger(RoomService.class);

    @Autowired
    private IRoomDao roomDao;

    @Autowired
    private IOrderDao orderDao;

    public RoomService() {
        super();
    }

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
    @Override
    public List<Room> getAllFreeForRoomType(Serializable roomTypeId, Date arrivalDate, Date eventsDate) {
        List<Room> allRoomsForRoomType = roomDao.getAllForRoomType(roomTypeId);
        // take rooms, that already booked
        List<Long> allBookedRoomId = orderDao
                .getAllBooked(roomTypeId, arrivalDate, eventsDate)
                .stream()
                .map(entry -> entry.getRoom().getId())
                .collect(Collectors.toList());

        List<Room> allFreeRoomsWithRoomType;

        // reduce booked rooms from allRoomsForRoomType
        if (allBookedRoomId.size() > 0) {
            allFreeRoomsWithRoomType = allRoomsForRoomType
                    .stream()
//                    .peek(System.out::println)
                    .filter(entry -> !allBookedRoomId.contains(entry.getId()))
//                    .peek(System.out::println)
                    .collect(Collectors.toList());


        } else {
            allFreeRoomsWithRoomType = allRoomsForRoomType;
        }

        log.info(String.format("There are [%s] free rooms for room type with id [%s] " +
                        "within arrival date [%s] and events date[%s].",
                allFreeRoomsWithRoomType.size(), roomTypeId, arrivalDate, eventsDate));
        return allFreeRoomsWithRoomType;
    }
}
