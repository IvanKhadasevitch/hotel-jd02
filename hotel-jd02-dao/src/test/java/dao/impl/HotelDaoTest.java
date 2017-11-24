package dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.Hotel;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class HotelDaoTest {

    @Autowired
    private HotelDao hotelDao;

    @Test
    @Rollback(value = true)
    public void getAll() {
        List<Hotel> list = hotelDao.getAll();

        assertNotNull(list);
        assertEquals("There are 3 Hotels in DB.", 3, list.size());

        hotelDao.getEm().clear();
    }
}
