package blinov_first.service.impl;

import blinov_first.dao.UserDao;
import blinov_first.dao.impl.UserDaoImpl;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.UserService;
import blinov_first.util.PasswordHasher;

public class UserServiceImpl implements UserService {
    private static final UserServiceImpl instance = new UserServiceImpl();

    private UserServiceImpl() {
    }

    public static UserServiceImpl getInstance() {
        return instance;
    }

    @Override
    public boolean authenticate(String login, String password) throws ServiceException {
        if (login == null || password == null) {
            return false;
        }

        UserDao userDao = UserDaoImpl.getInstance();
        try {
            String hashedInputPassword = PasswordHasher.hashPassword(password);

            return userDao.authenticate(login, hashedInputPassword);

        } catch (DaoException e) {
            throw new ServiceException("Ошибка аутентификации в сервисе", e);
        }
    }

    @Override
    public boolean registerNewUser(String login, String password, String email) throws ServiceException {
        if (login == null || login.length() < 3) return false;
        if (password == null || password.length() < 6) return false;

        UserDao userDao = UserDaoImpl.getInstance();
        try {
            if (userDao.findByLogin(login).isPresent()) {
                return false;
            }

            String hashedPassword = PasswordHasher.hashPassword(password);

            User newUser = new User(login, hashedPassword, email);
            return userDao.add(newUser);

        } catch (DaoException e) {
            throw new ServiceException("Ошибка при регистрации", e);
        }
    }
}