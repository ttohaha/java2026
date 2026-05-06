package blinov_first.service.impl;

import blinov_first.dao.UserDao;
import blinov_first.dao.impl.UserDaoImpl;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MailServiceFactory;
import blinov_first.service.MailService;
import blinov_first.service.UserService;
import blinov_first.util.TokenGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
    private static final UserServiceImpl INSTANCE = new UserServiceImpl();

    private UserServiceImpl() {}

    public static UserServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean authenticate(String login, String password) throws ServiceException {
        if (login == null || password == null) {
            return false;
        }
        try {
            UserDao userDao = UserDaoImpl.getInstance();
            if (!userDao.isUserActive(login)) {
                return false;
            }
            return userDao.authenticate(login, password);
        } catch (DaoException e) {
            throw new ServiceException("Authentication service error", e);
        }
    }

    @Override
    public boolean updateUserProfile(int userId, String lastname, String phone, String email)
            throws ServiceException {
        try {
            return UserDaoImpl.getInstance().updateProfile(userId, lastname, phone, email);
        } catch (DaoException e) {
            LOGGER.error("Profile update failed for user id: {}", userId, e);
            throw new ServiceException("Profile update failed", e);
        }
    }

    @Override
    public boolean registerNewUser(String login, String password, String email)
            throws ServiceException {
        return registerWithConfirmation(login, password, email);
    }

    @Override
    public boolean registerWithConfirmation(String login, String password, String email)
            throws ServiceException {
        if (isInvalidData(login, password, email)) {
            return false;
        }

        UserDao userDao = UserDaoImpl.getInstance();
        try {
            if (userDao.findByLogin(login).isPresent()) {
                return false;
            }

            User newUser = new User(login, password, email);
            newUser.setActive(false);

            if (!userDao.add(newUser)) {
                return false;
            }

            String token = TokenGenerator.generateSecureToken();
            if (!userDao.saveConfirmationToken(login, token)) {
                throw new ServiceException("Failed to save confirmation token");
            }

            MailService mailService = MailServiceFactory.getMailService();
            mailService.sendConfirmationEmail(newUser, token);

            return true;
        } catch (DaoException e) {
            throw new ServiceException("Registration service error", e);
        }
    }

    @Override
    public boolean confirmRegistration(String token) throws ServiceException {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            return UserDaoImpl.getInstance().activateUserByToken(token);
        } catch (DaoException e) {
            throw new ServiceException("Confirmation service error", e);
        }
    }

    @Override
    public String registerAndGetToken(String login, String password, String email)
            throws ServiceException {
        if (isInvalidData(login, password, email)) {
            return null;
        }
        try {
            UserDao dao = UserDaoImpl.getInstance();
            if (dao.findByLogin(login).isPresent()) {
                return null;
            }

            User newUser = new User(login, password, email);
            newUser.setActive(false);

            if (!dao.add(newUser)) {
                return null;
            }

            String token = TokenGenerator.generateSecureToken();
            if (!dao.saveConfirmationToken(login, token)) {
                throw new ServiceException("Failed to save confirmation token");
            }

            MailService mailService = MailServiceFactory.getMailService();
            mailService.sendConfirmationEmail(newUser, token);

            return token;
        } catch (DaoException e) {
            throw new ServiceException("Registration service error", e);
        }
    }

    @Override
    public List<User> findAllUsers() throws ServiceException {
        try {
            return UserDaoImpl.getInstance().findAll();
        } catch (DaoException e) {
            throw new ServiceException("Failed to fetch users", e);
        }
    }

    private boolean isInvalidData(String login, String password, String email) {
        return login == null || login.length() < 3
                || password == null || password.length() < 6
                || email == null || email.isEmpty();
    }
}