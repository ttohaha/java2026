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

    private static final String INSERT_ENTRY = "INSERT INTO phone_entries (user_id, contact_name, contact_phone, contact_email) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at FROM phone_entries WHERE id = ?";
    private static final String SELECT_BY_USER_ID = "SELECT id, user_id, contact_name, contact_phone, contact_email, created_at, updated_at FROM phone_entries WHERE user_id = ?";
    private static final String UPDATE_ENTRY = "UPDATE phone_entries SET contact_name = ?, contact_phone = ?, contact_email = ?, updated_at = NOW() WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM phone_entries WHERE id = ?";
    private static final String DELETE_ALL_BY_USER_ID = "DELETE FROM phone_entries WHERE user_id = ?";

    private PhoneEntryDaoImpl() {}

    public static PhoneEntryDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean add(PhoneEntry entry) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_ENTRY, Statement.RETURN_GENERATED_KEYS)) {
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
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public Optional<PhoneEntry> findById(int id) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to find phone entry by id: {}", id, e);
            throw new DaoException("Database error during findById", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<PhoneEntry> findByUserId(Long userId) throws DaoException {
        List<PhoneEntry> entries = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch entries for user: {}", userId, e);
            throw new DaoException("Database error during findByUserId", e);
        } finally {
            pool.releaseConnection(connection);
        }
        return entries;
    }

    @Override
    public boolean update(PhoneEntry entry) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_ENTRY)) {
            stmt.setString(1, entry.getContactName());
            stmt.setString(2, entry.getContactPhone());
            stmt.setString(3, entry.getContactEmail());
            // FIX: explicit cast to int instead of calling method on primitive
            stmt.setInt(4, (int) entry.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to update phone entry id: {}", entry.getId(), e);
            throw new DaoException("Database error during update", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete phone entry id: {}", id, e);
            throw new DaoException("Database error during delete", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    @Override
    public boolean deleteAllByUserId(Long userId) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_ALL_BY_USER_ID)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete all entries for user: {}", userId, e);
            throw new DaoException("Database error during deleteAllByUserId", e);
        } finally {
            pool.releaseConnection(connection);
        }
    }

    private PhoneEntry mapRow(ResultSet rs) throws SQLException {
        PhoneEntry entry = new PhoneEntry();
        entry.setId(rs.getInt("id"));
        entry.setUserId(rs.getLong("user_id"));
        entry.setContactName(rs.getString("contact_name"));
        entry.setContactPhone(rs.getString("contact_phone"));
        entry.setContactEmail(rs.getString("contact_email"));

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            entry.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            entry.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }

        return entry;
    }
}