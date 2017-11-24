package dao.impl;

import dao.IUserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.User;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class UserDao extends BaseDao<User> implements IUserDao {
    private static Logger log = Logger.getLogger(UserDao.class);

    @Autowired
    public UserDao() {

        super();
        clazz = User.class;
    }

    @Override
    public User getUserByEmail(String email) {
        CriteriaQuery<User> criteria = getCriteriaQuery();
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Root<User> root = (Root<User>) criteria.getRoots().toArray()[0];

        criteria.where(cb.equal(root.get("email"), email));

        User user = null;

        try {
            user = getSingleResultWhere(criteria);
            log.info(String.format("User with email [%s] is got from DB %s", email, user));
        } catch (NoResultException exception) {
            log.info("There is no user in DB with email: " + email);
        }


        return user;
    }

    @Override
    public boolean existWithEmail(String email) {

        return getUserByEmail(email) != null;
    }
}

