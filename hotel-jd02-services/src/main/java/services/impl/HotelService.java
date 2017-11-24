package services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Hotel;
import services.IHotelService;

@Service
@Transactional(transactionManager = "txManager")
public class HotelService extends BaseService<Hotel> implements IHotelService {
    public HotelService() {
        super();
    }

}
