package services.impl;

import dao.IUserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojos.User;
import services.IUserService;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(transactionManager = "txManager")
public class UserService extends BaseService<User> implements IUserService {
    private static Logger log = Logger.getLogger(UserService.class);

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$",
                    Pattern.CASE_INSENSITIVE);

    @Autowired
    private IUserDao userDao;


    public UserService() {
        super();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean validateEmail(String emailStr) {
        // if null email -> return false
        if (emailStr == null) {
            return false;
        }

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        boolean isValidEmail = matcher.matches();
        log.info(String.format("Email [%s] is valid [%s]", emailStr, isValidEmail));

        return isValidEmail;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean existWithEmail(String email) {
        return userDao.existWithEmail(email);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public User make(String name, String surName, Date birthDate, String email) {
        User user = new User();
        user.setName(name);
        user.setSurname(surName);
        user.setBirthDate(birthDate);
        user.setEmail(email);

        return user;
    }

}
