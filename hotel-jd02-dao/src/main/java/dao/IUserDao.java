package dao;



import pojos.User;


public interface IUserDao extends IDao<User> {
    User getUserByEmail(String email);

    boolean existWithEmail(String email);
}
