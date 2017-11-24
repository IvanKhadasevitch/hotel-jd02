package services.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojos.RoomType;
import services.IRoomTypeService;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RoomTypeServiceTest {

    @Autowired
    IRoomTypeService roomTypeService;

    @Test
    public void getAllForHotel() {
        List<RoomType> roomTypes = roomTypeService.getAllForHotel(1L);

        assertNotNull(roomTypes);
        assertEquals("Hotel with id [1L] has not [5] roomType",
                5, roomTypes.size());
    }
}
