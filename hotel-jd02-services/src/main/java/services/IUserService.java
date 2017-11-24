package services;


import pojos.User;

import java.sql.Date;

public interface IUserService extends IService<User> {
    boolean validateEmail(String emailStr);

    User getUserByEmail(String email);
    boolean existWithEmail(String email);

    /**
     *
     * @param name determine name of User
     * @param surName determine surname of User
     * @param birthDate determine birth Date of User
     * @param email determine email of User
     * @return new User with name, surName, birthDate, email
     */
    User make(String name, String surName, Date birthDate, String email);
}
