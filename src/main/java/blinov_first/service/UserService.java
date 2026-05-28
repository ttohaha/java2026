package blinov_first.service;

import blinov_first.entity.User;
import blinov_first.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findById(long id) throws ServiceException;

    List<User> findAll() throws ServiceException;

    /**
     * Registers a new user, generates a confirmation token and publishes
     * a {@code UserRegisteredEvent} so listeners can send a confirmation email.
     */
    boolean register(String login, String rawPassword,
                     String email, String phone) throws ServiceException;

    /**
     * Activates the account identified by the given confirmation token.
     *
     * @return {@code true} if a matching pending account was found and activated
     */
    boolean confirmRegistration(String token) throws ServiceException;

    boolean update(User user) throws ServiceException;
}
