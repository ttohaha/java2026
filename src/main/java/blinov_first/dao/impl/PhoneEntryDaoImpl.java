package blinov_first.dao.impl;

import blinov_first.dao.PhoneEntryDao;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.DaoException;
import blinov_first.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhoneEntryDaoImpl implements PhoneEntryDao {

    private static final Logger LOGGER = LogManager.getLogger(PhoneEntryDaoImpl.class);
    private static final PhoneEntryDaoImpl INSTANCE = new PhoneEntryDaoImpl();

    private static final String INSERT_ENTRY =
            "INSERT INTO phone_entries (user_id, contact_name, contact_phone, contact_email) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_ID =
            "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at " +
                    "FROM phone_entries WHERE id = ?";

    private static final String SELECT_BY_USER_ID =
            "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at " +
                    "FROM phone_entries WHERE user_id = ? ORDER BY contact_name";

    private static final String SELECT_BY_USER_ID_PAGED =
            "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at " +
                    "FROM phone_entries WHERE user_id = ? ORDER BY contact_name LIMIT ? OFFSET ?";

    private static final String COUNT_BY_USER_ID =
            "SELECT COUNT(*) FROM phone_entries WHERE user_id = ?";

    private static final String UPDATE_ENTRY =
            "UPDATE phone_entries SET contact_name = ?, contact_phone = ?, contact_email = ?, " +
                    "updated_at = NOW() WHERE id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM phone_entries WHERE id = ?";

    private static final String DELETE_ALL_BY_USER_ID =
            "DELETE FROM phone_entries WHERE user_id = ?";

    private static final String SEARCH_BY_USER_ID_AND_QUERY =
            "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at " +
                    "FROM phone_entries " +
                    "WHERE user_id = ? AND (contact_name LIKE ? OR contact_phone LIKE ?) " +
                    "ORDER BY contact_name LIMIT ?";

    private PhoneEntryDaoImpl() {}

    public static PhoneEntryDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean add(PhoneEntry entry) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     INSERT_ENTRY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, entry.getUserId());
            stmt.setString(2, entry.getContactName());
            stmt.setString(3, entry.getContactPhone());
            stmt.setString(4, entry.getContactEmail());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        entry.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert phone entry for user: {}", entry.getUserId(), e);
            throw new DaoException("Database error during phone entry insertion", e);
        }
    }

    @Override
    public Optional<PhoneEntry> findById(int id) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to find phone entry by id: {}", id, e);
            throw new DaoException("Database error during findById", e);
        }
        return Optional.empty();
    }

    @Override
    public List<PhoneEntry> findByUserId(Long userId) throws DaoException {
        List<PhoneEntry> entries = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch entries for user: {}", userId, e);
            throw new DaoException("Database error during findByUserId", e);
        }
        return entries;
    }

    @Override
    public List<PhoneEntry> findByUserIdPaged(Long userId, int offset, int limit) throws DaoException {
        List<PhoneEntry> entries = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID_PAGED)) {

            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch paged entries for user: {}", userId, e);
            throw new DaoException("Database error during findByUserIdPaged", e);
        }
        return entries;
    }

    @Override
    public int countByUserId(Long userId) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_BY_USER_ID)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to count entries for user: {}", userId, e);
            throw new DaoException("Database error during countByUserId", e);
        }
    }

    @Override
    public boolean update(PhoneEntry entry) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_ENTRY)) {

            stmt.setString(1, entry.getContactName());
            stmt.setString(2, entry.getContactPhone());
            stmt.setString(3, entry.getContactEmail());
            stmt.setInt(4, (int) entry.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to update phone entry id: {}", entry.getId(), e);
            throw new DaoException("Database error during update", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete phone entry id: {}", id, e);
            throw new DaoException("Database error during delete", e);
        }
    }

    @Override
    public List<PhoneEntry> searchByUserIdAndQuery(Long userId, String query, int limit) throws DaoException {
        List<PhoneEntry> entries = new ArrayList<>();
        String pattern = "%" + query + "%";
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(SEARCH_BY_USER_ID_AND_QUERY)) {

            stmt.setLong(1, userId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setInt(4, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to search entries for user: {} query: '{}'", userId, query, e);
            throw new DaoException("Database error during search", e);
        }
        return entries;
    }

    @Override
    public boolean deleteAllByUserId(Long userId) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_ALL_BY_USER_ID)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete all entries for user: {}", userId, e);
            throw new DaoException("Database error during deleteAllByUserId", e);
        }
    }

    private PhoneEntry mapRow(ResultSet rs) throws SQLException {
        PhoneEntry entry = new PhoneEntry();
        entry.setId(rs.getInt("id"));
        entry.setUserId(rs.getLong("user_id"));
        entry.setContactName(rs.getString("contact_name"));
        entry.setContactPhone(rs.getString("contact_phone"));
        entry.setContactEmail(rs.getString("contact_email"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) entry.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) entry.setUpdatedAt(updatedAt.toLocalDateTime());

        return entry;
    }
}
