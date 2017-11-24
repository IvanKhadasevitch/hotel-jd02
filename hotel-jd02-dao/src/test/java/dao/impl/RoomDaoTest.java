package dao.impl;

import dao.IRoomDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.Room;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class RoomDaoTest {
    @Autowired
    private BaseDao<Room> baseDao;

    @Autowired
    private IRoomDao roomDao;

    @Test
    @Rollback(value = true)
    public void getAllForRoomType() {
        List<Room> list = roomDao.getAllForRoomType(3L);

        assertNotNull(list);
        assertEquals("There are NOT [7] Rooms for RoomType with id [3] in DB.",
                7, list.size());

        // check absence Rooms in DB
        List<Room> emptyList = roomDao.getAllForRoomType(-1L);

        assertNotNull(emptyList);
        assertTrue("There ARE Rooms for RoomType with id [-1] in DB.",
                emptyList.isEmpty());

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void getAnyForRoomType() {
        Room room = roomDao.getAnyForRoomType(-1L);

        assertNull("There ARE ANY Room for RoomType with id [-1] in DB.",room);

        baseDao.getEm().clear();
    }
}
