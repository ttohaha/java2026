package blinov_first.dao;

import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    boolean authenticate(String login, String password) throws DaoException;
    Optional<User> findByLogin(String login) throws DaoException;
    boolean add(User user) throws DaoException;
    List<User> findAll() throws DaoException;
    boolean updateProfile(int userId, String lastname, String phone, String email) throws DaoException;
    boolean saveConfirmationToken(String login, String token) throws DaoException;
    Optional<User> findByConfirmationToken(String token) throws DaoException;
    boolean activateUserByToken(String token) throws DaoException;
    boolean isUserActive(String login) throws DaoException;
    boolean saveTelegramChatId(long userId, long chatId) throws DaoException;
}