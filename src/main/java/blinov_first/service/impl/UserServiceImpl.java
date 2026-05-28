package blinov_first.service.impl;

import blinov_first.dao.UserDao;
import blinov_first.entity.User;
import blinov_first.event.UserRegisteredEvent;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.UserService;
import blinov_first.util.TokenGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link UserService}.
 *
 * PATTERN — Singleton:   @Service — one instance per context.
 * PATTERN — Observer:    publishes {@link UserRegisteredEvent} after registration;
 *                        listeners react without this class knowing about them.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private static UserServiceImpl INSTANCE;

    private final UserDao                  userDao;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserDao userDao, ApplicationEventPublisher eventPublisher) {
        this.userDao        = userDao;
        this.eventPublisher = eventPublisher;
        INSTANCE            = this;
    }

    /** @deprecated Prefer Spring injection. */
    @Deprecated
    public static UserServiceImpl getInstance() {
        return INSTANCE;
    }

    // ----------------------------------------------------------------
    // UserService implementation
    // ----------------------------------------------------------------

    @Override
    public Optional<User> findById(long id) throws ServiceException {
        try {
            return userDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error finding user by id=" + id, e);
        }
    }

    @Override
    public List<User> findAll() throws ServiceException {
        try {
            return userDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Error fetching all users", e);
        }
    }

    @Override
    public boolean register(String login, String rawPassword,
                            String email, String phone) throws ServiceException {
        try {
            Optional<User> existing = userDao.findByLogin(login);
            if (existing.isPresent()) {
                LOGGER.warn("Registration attempt with existing login: {}", login);
                return false;
            }

            String token = TokenGenerator.generate();

            User user = new User();
            user.setLastname(login);
            user.setPassword(rawPassword);    // hashed by SQL SHA2(?, 256) in DAO
            user.setEmail(email);
            user.setPhone(phone);
            user.setActive(false);
            user.setRole("USER");
            user.setConfirmationToken(token);

            boolean saved = userDao.add(user);
            if (saved) {
                // Observer pattern: notify listeners (e.g. MailEventListener)
                eventPublisher.publishEvent(new UserRegisteredEvent(this, user, token));
                LOGGER.info("User registered: login={}, email={}", login, email);
            }
            return saved;
        } catch (DaoException e) {
            throw new ServiceException("Error during registration", e);
        }
    }

    @Override
    public boolean confirmRegistration(String token) throws ServiceException {
        try {
            return userDao.activateByToken(token);
        } catch (DaoException e) {
            throw new ServiceException("Error confirming registration", e);
        }
    }

    @Override
    public boolean update(User user) throws ServiceException {
        try {
            return userDao.update(user);
        } catch (DaoException e) {
            throw new ServiceException("Error updating user", e);
        }
    }
}
