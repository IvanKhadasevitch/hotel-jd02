package dao.impl;

import dao.IUserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pojos.User;


import static org.junit.Assert.*;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)

@Transactional(transactionManager = "txManager")
public class UserDaoTest  {
    @Autowired
    private BaseDao<User> baseDao;

    @Autowired
    private IUserDao userDao;


    @Test
    @Rollback(value = true)
    public void getUserByEmail() {
        User p = userDao.getUserByEmail("pushkin@mail.ru");
        assertNotNull(p);
        assertEquals("User with email [pushkin@mail.ru] exist in DB: ",
                "pushkin@mail.ru", p.getEmail());


        User noSuchUser = userDao.getUserByEmail("русский@mail.ru");
        assertNull("No User with email [русский@mail.ru] in DB: ", noSuchUser);

        baseDao.getEm().clear();
    }

    @Test
    @Rollback(value = true)
    public void existWithEmail() {
        boolean p = userDao.existWithEmail("pushkin@mail.ru");
        assertTrue("User with email [pushkin@mail.ru] exist in DB: ", p);


        boolean noSuchUser = userDao.existWithEmail("русский@mail.ru");
        assertFalse("No User with email [русский@mail.ru] in DB: ", noSuchUser);

        baseDao.getEm().clear();
    }

}
