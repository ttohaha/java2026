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

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);
    private static final UserDaoImpl INSTANCE = new UserDaoImpl();

    private static final String SQL_AUTHENTICATE =
            "SELECT COUNT(*) FROM users WHERE lastname = ? AND password = SHA2(?, 256)";

    private static final String SQL_FIND_BY_LOGIN =
            "SELECT idusers, lastname, password, email, phone, is_active FROM users WHERE lastname = ?";

    private static final String SQL_INSERT_USER =
            "INSERT INTO users (lastname, password, email) VALUES (?, SHA2(?, 256), ?)";

    private static final String SQL_FIND_ALL =
            "SELECT idusers, lastname, email, phone, is_active FROM users";

    private static final String SQL_SAVE_TOKEN =
            "UPDATE users SET confirmation_token = ?, token_created_at = NOW() WHERE lastname = ?";

    private static final String SQL_FIND_BY_TOKEN =
            "SELECT idusers, lastname, password, email, is_active FROM users WHERE confirmation_token = ?";

    private static final String SQL_FIND_USER_ID_BY_TOKEN =
            "SELECT idusers FROM users WHERE confirmation_token = ? AND is_active = FALSE";

    private static final String SQL_ACTIVATE_BY_TOKEN =
            "UPDATE users SET is_active = TRUE, confirmation_token = NULL, token_created_at = NULL " +
                    "WHERE confirmation_token = ?";

    private static final String SQL_IS_ACTIVE =
            "SELECT is_active FROM users WHERE lastname = ?";

    private static final String SQL_UPDATE_PROFILE =
            "UPDATE users SET lastname = ?, phone = ?, email = ? WHERE idusers = ?";

    private static final String SQL_SAVE_TELEGRAM_CHAT_ID =
            "UPDATE users SET telegram_chat_id = ? WHERE idusers = ?";

    private UserDaoImpl() {}

    public static UserDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean authenticate(String login, String password) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_AUTHENTICATE)) {

            statement.setString(1, login);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Authentication failed for login: {}", login, e);
            throw new DaoException("Database error during authentication", e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_LOGIN)) {

            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFull(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("findByLogin failed for: {}", login, e);
            throw new DaoException("Database error in findByLogin", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean add(User user) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

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
            LOGGER.error("Failed to insert user: {}", user.getLogin(), e);
            throw new DaoException("Database error during user insertion", e);
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(SQL_FIND_ALL)) {

            while (rs.next()) {
                users.add(mapPublic(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("findAll failed", e);
            throw new DaoException("Database error in findAll", e);
        }
        return users;
    }

    @Override
    public boolean saveConfirmationToken(String login, String token) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE_TOKEN)) {

            statement.setString(1, token);
            statement.setString(2, login);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to save token for: {}", login, e);
            throw new DaoException("Database error during token save", e);
        }
    }

    @Override
    public Optional<User> findByConfirmationToken(String token) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_TOKEN)) {

            statement.setString(1, token);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFull(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("findByToken failed", e);
            throw new DaoException("Database error in findByConfirmationToken", e);
        }
        return Optional.empty();
    }

    public java.util.Optional<Long> findUserIdByToken(String token) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SQL_FIND_USER_ID_BY_TOKEN)) {

            statement.setString(1, token);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return java.util.Optional.of(rs.getLong("idusers"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("findUserIdByToken failed", e);
            throw new DaoException("Database error in findUserIdByToken", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public boolean activateUserByToken(String token) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ACTIVATE_BY_TOKEN)) {

            statement.setString(1, token);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to activate user by token", e);
            throw new DaoException("Database error during activation", e);
        }
    }

    @Override
    public boolean isUserActive(String login) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_IS_ACTIVE)) {

            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_active");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("isUserActive failed for: {}", login, e);
            throw new DaoException("Database error in isUserActive", e);
        }
        return false;
    }

    @Override
    public boolean updateProfile(int userId, String lastname, String phone, String email)
            throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_PROFILE)) {

            statement.setString(1, lastname);
            statement.setString(2, phone);
            statement.setString(3, email);
            statement.setInt(4, userId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("updateProfile failed for userId: {}", userId, e);
            throw new DaoException("Database error during profile update", e);
        }
    }

    @Override
    public boolean saveTelegramChatId(long userId, long chatId) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE_TELEGRAM_CHAT_ID)) {

            statement.setLong(1, chatId);
            statement.setLong(2, userId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Failed to save telegram_chat_id for userId={}", userId, e);
            throw new DaoException("Database error during telegram_chat_id save", e);
        }
    }

    private User mapFull(ResultSet rs) throws SQLException {
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

    private User mapPublic(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("lastname"), "", rs.getString("email"));
        user.setId(rs.getLong("idusers"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}