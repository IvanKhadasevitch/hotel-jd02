package dao.impl;

import dao.IHotelDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.Hotel;


@Repository
public class HotelDao extends BaseDao<Hotel> implements IHotelDao{
    private static Logger log = Logger.getLogger(HotelDao.class);

    @Autowired
    public HotelDao() {
        super();
        clazz = Hotel.class;
    }

}
