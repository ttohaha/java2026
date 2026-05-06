package blinov_first.dao.impl;

import blinov_first.dao.MediaFileDao;
import blinov_first.entity.MediaFile;
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

public class MediaFileDaoImpl implements MediaFileDao {

    private static final Logger LOGGER = LogManager.getLogger(MediaFileDaoImpl.class);
    private static final MediaFileDaoImpl INSTANCE = new MediaFileDaoImpl();

    private static final String INSERT_FILE =
            "INSERT INTO media_files (user_id, stored_filename, original_filename, content_type, file_size, file_path) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
            "SELECT id, user_id, stored_filename, original_filename, content_type, file_size, file_path, upload_date FROM media_files WHERE id = ?";
    private static final String SELECT_BY_USER_ID =
            "SELECT id, user_id, stored_filename, original_filename, content_type, file_size, file_path, upload_date FROM media_files WHERE user_id = ?";
    private static final String DELETE_BY_ID_AND_USER =
            "DELETE FROM media_files WHERE id = ? AND user_id = ?";

    private MediaFileDaoImpl() {}

    public static MediaFileDaoImpl getInstance() { return INSTANCE; }

    @Override
    public boolean add(MediaFile file) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        try (Connection connection = pool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_FILE, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, file.getUserId());
            stmt.setString(2, file.getStoredFilename());
            stmt.setString(3, file.getOriginalFilename());
            stmt.setString(4, file.getContentType());
            stmt.setLong(5, file.getFileSize());
            stmt.setString(6, file.getFilePath());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        file.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert media file record for user: {}", file.getUserId(), e);
            throw new DaoException("Database error during media file insertion", e);
        }
    }

    @Override
    public Optional<MediaFile> findById(int id) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        try (Connection connection = pool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to find media file by id: {}", id, e);
            throw new DaoException("Database error during findById", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MediaFile> findByUserId(Long userId) throws DaoException {
        List<MediaFile> files = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        try (Connection connection = pool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    files.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch media files for user: {}", userId, e);
            throw new DaoException("Database error during findByUserId", e);
        }
        return files;
    }

    @Override
    public boolean deleteById(int id, Long userId) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        try (Connection connection = pool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID_AND_USER)) {
            stmt.setInt(1, id);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete media file record: {}", id, e);
            throw new DaoException("Database error during delete", e);
        }
    }

    private MediaFile mapRow(ResultSet rs) throws SQLException {
        MediaFile file = new MediaFile();
        file.setId(rs.getInt("id"));
        file.setUserId(rs.getLong("user_id"));
        file.setStoredFilename(rs.getString("stored_filename"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setContentType(rs.getString("content_type"));
        file.setFileSize(rs.getLong("file_size"));
        file.setFilePath(rs.getString("file_path"));

        Timestamp uploadTs = rs.getTimestamp("upload_date");
        if (uploadTs != null) {
            file.setUploadDate(uploadTs.toLocalDateTime());
        }
        return file;
    }
}