package blinov_first.dao.impl;

import blinov_first.dao.UserDao;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link UserDao} using Spring's {@link JdbcTemplate}.
 *
 * PATTERN — Singleton (GoF):
 *   Annotated with {@code @Repository}; Spring creates and manages exactly one
 *   instance.  The static {@code getInstance()} method is kept only for
 *   backward compatibility with legacy code still present in the project.
 */
@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    /* Backward-compat holder — set on construction by Spring */
    private static UserDaoImpl INSTANCE;

    private final JdbcTemplate jdbc;

    public UserDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        INSTANCE  = this;   // supports legacy getInstance() callers
    }

    /** @deprecated Prefer Spring injection. */
    @Deprecated
    public static UserDaoImpl getInstance() {
        return INSTANCE;
    }

    // ----------------------------------------------------------------
    // SQL constants
    // ----------------------------------------------------------------

    private static final String SELECT_ALL =
            "SELECT id, lastname, password, email, phone, active, role, confirmation_token " +
            "FROM users";

    private static final String SELECT_BY_ID =
            SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_BY_LOGIN =
            SELECT_ALL + " WHERE lastname = ?";

    private static final String SELECT_BY_TOKEN =
            SELECT_ALL + " WHERE confirmation_token = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (lastname, password, email, phone, active, role, confirmation_token) " +
            "VALUES (?, SHA2(?, 256), ?, ?, ?, ?, ?)";

    private static final String UPDATE_USER =
            "UPDATE users SET lastname = ?, email = ?, phone = ? WHERE id = ?";

    private static final String ACTIVATE_BY_TOKEN =
            "UPDATE users SET active = TRUE, confirmation_token = NULL " +
            "WHERE confirmation_token = ?";

    // ----------------------------------------------------------------
    // RowMapper
    // ----------------------------------------------------------------

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setLastname(rs.getString("lastname"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setActive(rs.getBoolean("active"));
        u.setRole(rs.getString("role"));
        u.setConfirmationToken(rs.getString("confirmation_token"));
        return u;
    };

    // ----------------------------------------------------------------
    // UserDao implementation
    // ----------------------------------------------------------------

    @Override
    public Optional<User> findById(long id) throws DaoException {
        try {
            List<User> results = jdbc.query(SELECT_BY_ID, ROW_MAPPER, id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (DataAccessException e) {
            LOGGER.error("findById failed for id={}", id, e);
            throw new DaoException("Database error in findById", e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        try {
            List<User> results = jdbc.query(SELECT_BY_LOGIN, ROW_MAPPER, login);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (DataAccessException e) {
            LOGGER.error("findByLogin failed for login={}", login, e);
            throw new DaoException("Database error in findByLogin", e);
        }
    }

    @Override
    public Optional<User> findByConfirmationToken(String token) throws DaoException {
        try {
            List<User> results = jdbc.query(SELECT_BY_TOKEN, ROW_MAPPER, token);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (DataAccessException e) {
            LOGGER.error("findByConfirmationToken failed", e);
            throw new DaoException("Database error in findByConfirmationToken", e);
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        try {
            return jdbc.query(SELECT_ALL, ROW_MAPPER);
        } catch (DataAccessException e) {
            LOGGER.error("findAll failed", e);
            throw new DaoException("Database error in findAll", e);
        }
    }

    @Override
    public boolean add(User user) throws DaoException {
        try {
            KeyHolder keys = new GeneratedKeyHolder();
            int rows = jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getLastname());
                ps.setString(2, user.getPassword());   // SHA2 applied in SQL
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPhone());
                ps.setBoolean(5, user.isActive());
                ps.setString(6, user.getRole() != null ? user.getRole() : "USER");
                ps.setString(7, user.getConfirmationToken());
                return ps;
            }, keys);

            if (rows > 0 && keys.getKey() != null) {
                user.setId(keys.getKey().longValue());
            }
            return rows > 0;
        } catch (DataAccessException e) {
            LOGGER.error("add failed for user={}", user.getLastname(), e);
            throw new DaoException("Database error in add", e);
        }
    }

    @Override
    public boolean update(User user) throws DaoException {
        try {
            int rows = jdbc.update(UPDATE_USER,
                    user.getLastname(), user.getEmail(),
                    user.getPhone(), user.getId());
            return rows > 0;
        } catch (DataAccessException e) {
            LOGGER.error("update failed for id={}", user.getId(), e);
            throw new DaoException("Database error in update", e);
        }
    }

    @Override
    public boolean activateByToken(String token) throws DaoException {
        try {
            return jdbc.update(ACTIVATE_BY_TOKEN, token) > 0;
        } catch (DataAccessException e) {
            LOGGER.error("activateByToken failed", e);
            throw new DaoException("Database error in activateByToken", e);
        }
    }
}
