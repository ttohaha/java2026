package blinov_first.dao.impl;

import blinov_first.dao.UserDao;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import blinov_first.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private static final UserDaoImpl instance = new UserDaoImpl();

    private static final String SELECT_PASSWORD_BY_LOGIN =
            "SELECT password FROM users WHERE lastname = ?";

    private static final String SELECT_USER_BY_LOGIN =
            "SELECT idusers, lastname, password, email, phone, is_active FROM users WHERE lastname = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (lastname, password, email) VALUES (?, ?, ?)";

    // FIX: this query intentionally excludes password for security
    private static final String SELECT_ALL_USERS =
            "SELECT idusers, lastname, email, phone, is_active FROM users";

    private static final String UPDATE_TOKEN_BY_LOGIN =
            "UPDATE users SET confirmation_token = ?, token_created_at = NOW() WHERE lastname = ?";

    private static final String SELECT_USER_BY_TOKEN =
            "SELECT idusers, lastname, password, email, is_active FROM users WHERE confirmation_token = ?";

    private static final String ACTIVATE_USER_BY_TOKEN =
            "UPDATE users SET is_active = TRUE, confirmation_token = NULL, token_created_at = NULL WHERE confirmation_token = ?";

    private static final String SELECT_IS_ACTIVE_BY_LOGIN =
            "SELECT is_active FROM users WHERE lastname = ?";

    private static final String UPDATE_PROFILE_BY_ID =
            "UPDATE users SET lastname = ?, phone = ?, email = ? WHERE idusers = ?";

    private UserDaoImpl() {}

    public static UserDaoImpl getInstance() {
        return instance;
    }

    @Override
    public boolean authenticate(String login, String password) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_PASSWORD_BY_LOGIN)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
                    return storedHash != null && storedHash.equals(password);
                }
            }
        } catch (SQLException e) {
            logger.error("Authentication query failed for user: {}", login, e);
            throw new DaoException("Database error during authentication", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return false;
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_LOGIN)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowWithPassword(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Find user by login failed: {}", login, e);
            throw new DaoException("Database error during findByLogin", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public boolean add(User user) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            int rows = statement.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getLong(1));
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Failed to insert user: {}", user.getLogin(), e);
            throw new DaoException("Database error during user insertion", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS)) {

            while (resultSet.next()) {
                // FIX: use mapper that does not expect password column
                users.add(mapRowWithoutPassword(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch all users", e);
            throw new DaoException("Database error during findAll", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return users;
    }

    @Override
    public boolean saveConfirmationToken(String login, String token) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TOKEN_BY_LOGIN)) {
            statement.setString(1, token);
            statement.setString(2, login);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to save confirmation token for user: {}", login, e);
            throw new DaoException("Database error during token save", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public Optional<User> findByConfirmationToken(String token) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_TOKEN)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowWithPassword(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Find user by token failed: {}", token, e);
            throw new DaoException("Database error during findByConfirmationToken", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public boolean activateUserByToken(String token) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(ACTIVATE_USER_BY_TOKEN)) {
            statement.setString(1, token);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to activate user by token: {}", token, e);
            throw new DaoException("Database error during user activation", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public boolean isUserActive(String login) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_IS_ACTIVE_BY_LOGIN)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_active");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to check user active status: {}", login, e);
            throw new DaoException("Database error during active check", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return false;
    }

    @Override
    public boolean updateProfile(int userId, String lastname, String phone, String email) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PROFILE_BY_ID)) {
            statement.setString(1, lastname);
            statement.setString(2, phone);
            statement.setString(3, email);
            statement.setInt(4, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update profile for user id: {}", userId, e);
            throw new DaoException("Database error during profile update", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    // FIX: mapper for queries that INCLUDE password column
    private User mapRowWithPassword(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("lastname"),
                rs.getString("password"),
                rs.getString("email")
        );
        user.setId(rs.getLong("idusers"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }

    // FIX: mapper for queries that EXCLUDE password column (e.g., findAll)
    private User mapRowWithoutPassword(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("lastname"),
                "protected", // placeholder, password not loaded for security
                rs.getString("email")
        );
        user.setId(rs.getLong("idusers"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}