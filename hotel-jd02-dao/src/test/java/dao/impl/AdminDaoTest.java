package dao.impl;

import dao.IAdminDao;
import dao.IHotelDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.Admin;
import pojos.Hotel;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class AdminDaoTest {

    @Autowired
    private JpaTransactionManager jpaTransactionManager;

    @Autowired
    private BaseDao<Admin> baseDao;

    @Autowired
    private IAdminDao adminDao;
//    private AdminDao adminDao;
    @Autowired
    private IHotelDao hotelDao;
//    private HotelDao hotelDao;


    @Test
//    @Rollback(value = false)
    public void crudAdminTest() {
        jpaTransactionManager.getDataSource();

        // get current entityManager
        EntityManager entityManager = baseDao.getEm();

        // make new Admin
        Admin admin = new Admin("TestAdmin", "1");

        Hotel hotel = hotelDao.get(1L);
        admin.setHotel(hotel);

        // save Admin in DB and persist
        Admin persistent = adminDao.add(admin);

        assertNotNull(persistent.getId());

        //clear persistence context, make persistent(admin) detached
        entityManager.clear();

        // check exist in DB saved entity(admin)
        persistent = adminDao.get(admin.getId());
        assertEquals("Admin not persist", admin, persistent);

        // update (merge entity to persistence context) entity(admin)
        persistent = adminDao.update(persistent);

        // remove Admin from DB
        adminDao.delete(persistent.getId());

        // check Correctness of removal
        Admin getIt = adminDao.get(persistent.getId());

        assertNull("Admin after deletion is not null", getIt);

        //clear persistence context, make persistent(admin) detached
        entityManager.clear();
    }

    @Test
    @Rollback(value = true)
    public void getAllAdminTest() {
        // get current entityManager
        EntityManager entityManager = baseDao.getEm();

        // take All Admins from DB
        List<Admin> adminList = adminDao.getAll();

        assertNotNull(adminList);
        assertEquals("In DB not 5 admins: ", 5, adminList.size());

        //clear persistence context
        entityManager.clear();

    }
}
