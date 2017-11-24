package dao.impl;

import dao.IRoomTypeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.RoomType;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class RoomTypeDaoTest {
    @Autowired
    private BaseDao<RoomType> baseDao;

    @Autowired
    private IRoomTypeDao roomTypeDao;

    @Test
    @Rollback(value = true)
    public void getAllForHotel() {
        List<RoomType> list = roomTypeDao.getAllForHotel(3L);

        assertNotNull(list);
        assertEquals("There are 2 RoomTypes for Hotel with id [3] in DB.",
                2, list.size());

        // check absence RoomTypes in DB
        List<RoomType> emptyList = roomTypeDao.getAllForHotel(-1L);

        assertNotNull(emptyList);
        assertTrue("There are NO RoomTypes for Hotel with id [-1] in DB.",
                emptyList.isEmpty());

        baseDao.getEm().clear();
    }

}
