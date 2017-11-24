package services.impl;

import dao.IRoomTypeDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.RoomType;
import services.IRoomTypeService;

import java.io.Serializable;
import java.util.List;

@Service
@Transactional(transactionManager = "txManager")
public class RoomTypeService extends BaseService<RoomType> implements IRoomTypeService {
    private static Logger log = Logger.getLogger(RoomTypeService.class);

    @Autowired
    private IRoomTypeDao roomTypeDao;

    public RoomTypeService() {
        super();
    }

    /**
     *
     * @param hotelId determine Hotel id where are rooms with room type
     * @return a list of all room types belonging to the hotel.
     *
     * returns an empty list if there are no room types belonging to the hotel
     */
    @Override
    public List<RoomType> getAllForHotel(Serializable hotelId) {

        return roomTypeDao.getAllForHotel(hotelId);
    }
}
