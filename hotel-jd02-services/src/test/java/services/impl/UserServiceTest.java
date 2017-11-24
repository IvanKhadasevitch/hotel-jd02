package services.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojos.User;
import services.IUserService;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
    @Autowired
    IUserService userService;

    @Test
    public void crudUser() {
        // make new User
        User user = userService.make("TestUser", "TestSurnameUser",
                Date.valueOf("1970-01-01"), "testUser@gmail.com");
        user.setPassword("1");
        assertNull("New user has not null id: ", user.getId());

        // add User to DB
        User addUser = userService.add(user);
        addUser.setFullName(addUser.getName() + " " + addUser.getSurname());

        assertNotNull("Added user has null id.", addUser.getId());

        // take added user from DB
        User userFromDB = userService.get(addUser.getId());

        assertNotNull("Can't find added user in DB", userFromDB);
        assertEquals("User From DB not equals to saved user", addUser, userFromDB);

        // update user
        userFromDB.setName("UpdateUserName");
        User updatedUser = userService.update(userFromDB);

        assertNotNull("Updated user is null.", updatedUser);
        assertEquals("User wasn't updated", userFromDB.getName(), updatedUser.getName());

        // delete user from DB
        userService.delete(updatedUser.getId());

        // check correctness deleting
        userFromDB = userService.get(updatedUser.getId());

        assertNull("Updated user don't deleted from DB.", userFromDB);

    }

    @Test
    public void getAll() {
        // make new User
        User user = userService.make("TestUser", "TestSurnameUser",
                Date.valueOf("1970-01-01"), "testUser@gmail.com");
        user.setPassword("1");
        assertNull("New user has not null id: ", user.getId());

        List<User> beforeSaveList = userService.getAll();

        // add User to DB
        User addUser = userService.add(user);

        List<User> afterSaveList = userService.getAll();

        assertEquals("List size after safe User minus List size before safe " +
                "not equals [1]", 1, afterSaveList.size() - beforeSaveList.size());

        // delete saved user
        userService.delete(addUser.getId());

        // check correct deleting user from DB
        User fromDB = userService.get(addUser.getId());

        assertNull("User after delete not null", fromDB);
    }

    @Test
    public void validateEmail() {
        assertFalse("Valid null email", userService.validateEmail(null));

        assertTrue("Not valid email [test@mail.ru]",
                userService.validateEmail("test@mail.ru"));

        assertFalse("Not valid email [русский@mail.ru]",
                userService.validateEmail("русский@mail.ru"));
    }

    @Test
    public void getUserByEmail() {
        User user = userService.getUserByEmail("jordan@gmail.com");
        assertNotNull("No such user with email [jordan@gmail.com]", user);
        assertTrue("Found user with email not equals to [jordan@gmail.com]",
                "jordan@gmail.com".equals(user.getEmail()));

        assertNull("There is user with email [русский@mail.ru]",
                userService.getUserByEmail("русский@mail.ru"));
    }

    @Test
    public void existWithEmail() {
        assertTrue("No such user with email [jordan@gmail.com]",
                userService.existWithEmail("jordan@gmail.com"));

        assertFalse("There is user with email [русский@mail.ru]",
                userService.existWithEmail("русский@mail.ru"));
    }

}
