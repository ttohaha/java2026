package blinov_first.dao.impl;

import blinov_first.dao.PhoneEntryDao;
import blinov_first.entity.PhoneEntry;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link PhoneEntryDao} using Spring {@link JdbcTemplate}.
 *
 * PATTERN — Singleton: @Repository ensures one instance per Spring context.
 */
@Repository
public class PhoneEntryDaoImpl implements PhoneEntryDao {

    private static final Logger LOGGER = LogManager.getLogger(PhoneEntryDaoImpl.class);

    private static PhoneEntryDaoImpl INSTANCE;

    private final JdbcTemplate jdbc;

    public PhoneEntryDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        INSTANCE  = this;
    }

    /** @deprecated Prefer Spring injection. */
    @Deprecated
    public static PhoneEntryDaoImpl getInstance() {
        return INSTANCE;
    }

    // ----------------------------------------------------------------
    // SQL constants
    // ----------------------------------------------------------------

    private static final String SELECT_BASE =
            "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at " +
            "FROM phone_book";

    private static final String SELECT_BY_ID =
            SELECT_BASE + " WHERE id = ? AND user_id = ?";

    private static final String SELECT_BY_USER =
            SELECT_BASE + " WHERE user_id = ? ORDER BY contact_name";

    private static final String SELECT_BY_USER_PAGED =
            SELECT_BASE + " WHERE user_id = ? ORDER BY contact_name LIMIT ? OFFSET ?";

    private static final String COUNT_BY_USER =
            "SELECT COUNT(*) FROM phone_book WHERE user_id = ?";

    private static final String SEARCH =
            SELECT_BASE +
            " WHERE user_id = ? AND (" +
            "  contact_name  LIKE ? OR" +
            "  contact_phone LIKE ? OR" +
            "  contact_email LIKE ?) " +
            "ORDER BY contact_name";

    private static final String INSERT_ENTRY =
            "INSERT INTO phone_book (user_id, contact_name, contact_phone, contact_email, created_at) " +
            "VALUES (?, ?, ?, ?, NOW())";

    private static final String UPDATE_ENTRY =
            "UPDATE phone_book SET contact_name = ?, contact_phone = ?, contact_email = ? " +
            "WHERE id = ? AND user_id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM phone_book WHERE id = ? AND user_id = ?";

    // ----------------------------------------------------------------
    // RowMapper
    // ----------------------------------------------------------------

    private static final RowMapper<PhoneEntry> ROW_MAPPER = (rs, rowNum) -> {
        PhoneEntry e = new PhoneEntry();
        e.setId(rs.getLong("id"));
        e.setUserId(rs.getLong("user_id"));
        e.setContactName(rs.getString("contact_name"));
        e.setContactPhone(rs.getString("contact_phone"));
        e.setContactEmail(rs.getString("contact_email"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            e.setCreatedAt(ts.toLocalDateTime());
        }
        return e;
    };

    // ----------------------------------------------------------------
    // PhoneEntryDao implementation
    // ----------------------------------------------------------------

    @Override
    public Optional<PhoneEntry> findById(long id) throws DaoException {
        try {
            List<PhoneEntry> rows = jdbc.query(
                    SELECT_BASE + " WHERE id = ?", ROW_MAPPER, id);
            return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
        } catch (DataAccessException ex) {
            LOGGER.error("findById failed for id={}", id, ex);
            throw new DaoException("Database error in findById", ex);
        }
    }

    @Override
    public List<PhoneEntry> findByUserId(Long userId) throws DaoException {
        try {
            return jdbc.query(SELECT_BY_USER, ROW_MAPPER, userId);
        } catch (DataAccessException ex) {
            LOGGER.error("findByUserId failed for userId={}", userId, ex);
            throw new DaoException("Database error in findByUserId", ex);
        }
    }

    @Override
    public List<PhoneEntry> findByUserIdPaged(Long userId, int offset, int limit)
            throws DaoException {
        try {
            return jdbc.query(SELECT_BY_USER_PAGED, ROW_MAPPER, userId, limit, offset);
        } catch (DataAccessException ex) {
            LOGGER.error("findByUserIdPaged failed for userId={}", userId, ex);
            throw new DaoException("Database error in findByUserIdPaged", ex);
        }
    }

    @Override
    public int countByUserId(Long userId) throws DaoException {
        try {
            Integer count = jdbc.queryForObject(COUNT_BY_USER, Integer.class, userId);
            return count != null ? count : 0;
        } catch (DataAccessException ex) {
            LOGGER.error("countByUserId failed for userId={}", userId, ex);
            throw new DaoException("Database error in countByUserId", ex);
        }
    }

    @Override
    public List<PhoneEntry> searchByUserIdAndQuery(Long userId, String query)
            throws DaoException {
        try {
            String pattern = "%" + query + "%";
            return jdbc.query(SEARCH, ROW_MAPPER,
                    userId, pattern, pattern, pattern);
        } catch (DataAccessException ex) {
            LOGGER.error("search failed for userId={}, query={}", userId, query, ex);
            throw new DaoException("Database error in search", ex);
        }
    }

    @Override
    public boolean add(PhoneEntry entry) throws DaoException {
        try {
            KeyHolder keys = new GeneratedKeyHolder();
            int rows = jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        INSERT_ENTRY, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, entry.getUserId());
                ps.setString(2, entry.getContactName());
                ps.setString(3, entry.getContactPhone());
                ps.setString(4, entry.getContactEmail());
                return ps;
            }, keys);

            if (rows > 0 && keys.getKey() != null) {
                entry.setId(keys.getKey().longValue());
            }
            return rows > 0;
        } catch (DataAccessException ex) {
            LOGGER.error("add failed for entry={}", entry, ex);
            throw new DaoException("Database error in add", ex);
        }
    }

    @Override
    public boolean update(PhoneEntry entry) throws DaoException {
        try {
            int rows = jdbc.update(UPDATE_ENTRY,
                    entry.getContactName(),
                    entry.getContactPhone(),
                    entry.getContactEmail(),
                    entry.getId(),
                    entry.getUserId());
            return rows > 0;
        } catch (DataAccessException ex) {
            LOGGER.error("update failed for id={}", entry.getId(), ex);
            throw new DaoException("Database error in update", ex);
        }
    }

    @Override
    public boolean deleteById(long id, Long userId) throws DaoException {
        try {
            return jdbc.update(DELETE_BY_ID, id, userId) > 0;
        } catch (DataAccessException ex) {
            LOGGER.error("deleteById failed for id={}", id, ex);
            throw new DaoException("Database error in deleteById", ex);
        }
    }
}
