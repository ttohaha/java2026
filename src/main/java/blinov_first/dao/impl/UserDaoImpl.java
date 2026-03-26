package blinov_first.dao.impl;

import blinov_first.dao.BaseDao;
import blinov_first.dao.UserDao;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import blinov_first.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends BaseDao<User> implements UserDao {
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    private static final String SELECT_PASSWORD_BY_LASTNAME = "SELECT password FROM users WHERE lastname = ?";
    private static final String SELECT_ALL_USERS = "SELECT lastname, password, email, phone FROM users";
    private static final String SELECT_USER_BY_LASTNAME = "SELECT lastname, password, email FROM users WHERE lastname = ?";
    private static final String INSERT_USER = "INSERT INTO users (lastname, password, email) VALUES (?, ?, ?)";

    private static final UserDaoImpl instance = new UserDaoImpl();

    private UserDaoImpl() {
    }

    public static UserDaoImpl getInstance() {
        return instance;
    }

    @Override
    public boolean authenticate(String login, String password) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        boolean match = false;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PASSWORD_BY_LASTNAME)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String passFromDb = resultSet.getString("password");
                    match = password.equals(passFromDb);
                }
            }
        } catch (SQLException e) {
            logger.error("Authentication failed for: {}", login, e);
            throw new DaoException("SQL authenticate failed", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return match;
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_LASTNAME)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User(
                            resultSet.getString("lastname"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Find by lastname failed: {}", login, e);
            throw new DaoException("SQL findByLogin failed", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public boolean add(User user) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER)) {
            statement.setString(1, user.getLogin()); // Это пойдет в колонку lastname
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Insert user failed: {}", user.getLogin(), e);
            throw new DaoException("SQL insert failed", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        String sql = "SELECT lastname, email, phone FROM users";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String dbLastname = resultSet.getString("lastname");
                String dbEmail = resultSet.getString("email");
                String dbPhone = resultSet.getString("phone");

                User user = new User(dbLastname, "protected", dbEmail);
                user.setLastname(dbLastname);
                user.setPhone(dbPhone);

                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Error in findAll", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return users;
    }

    @Override
    public boolean insert(User user) {
        try {
            return add(user);
        } catch (DaoException e) {
            return false;
        }
    }

    @Override
    public boolean delete(User user) {
        throw new UnsupportedOperationException("delete unsupported");
    }

    @Override
    public User update(User user) {
        return null;
    }
}