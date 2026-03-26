package blinov_first.dao;

import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import java.util.Optional;

public interface UserDao {
    boolean authenticate(String login, String password) throws DaoException;

    Optional<User> findByLogin(String login) throws DaoException;

    boolean add(User user) throws DaoException;
}