package blinov_first.dao;

import blinov_first.entity.User;
import blinov_first.exception.DaoException;

import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for {@link User} entities.
 */
public interface UserDao {

    Optional<User> findById(long id) throws DaoException;

    /** Finds a user by their login name (stored in the {@code lastname} column). */
    Optional<User> findByLogin(String login) throws DaoException;

    /** Finds a user by their email confirmation token. */
    Optional<User> findByConfirmationToken(String token) throws DaoException;

    List<User> findAll() throws DaoException;

    boolean add(User user) throws DaoException;

    boolean update(User user) throws DaoException;

    boolean activateByToken(String token) throws DaoException;
}
