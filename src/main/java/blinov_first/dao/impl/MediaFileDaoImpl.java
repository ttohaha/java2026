package blinov_first.dao.impl;

import blinov_first.dao.MediaFileDao;
import blinov_first.entity.MediaFile;
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
 * JDBC implementation of {@link MediaFileDao} using Spring {@link JdbcTemplate}.
 *
 * PATTERN — Singleton: @Repository ensures one instance per Spring context.
 */
@Repository
public class MediaFileDaoImpl implements MediaFileDao {

    private static final Logger LOGGER = LogManager.getLogger(MediaFileDaoImpl.class);

    private static MediaFileDaoImpl INSTANCE;

    private final JdbcTemplate jdbc;

    public MediaFileDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        INSTANCE  = this;
    }

    /** @deprecated Prefer Spring injection. */
    @Deprecated
    public static MediaFileDaoImpl getInstance() {
        return INSTANCE;
    }

    // ----------------------------------------------------------------
    // SQL constants
    // ----------------------------------------------------------------

    private static final String SELECT_BASE =
            "SELECT id, user_id, stored_filename, original_filename, " +
            "       content_type, file_size, file_path, upload_date " +
            "FROM media_files";

    private static final String SELECT_BY_ID =
            SELECT_BASE + " WHERE id = ?";

    private static final String SELECT_BY_USER =
            SELECT_BASE + " WHERE user_id = ? ORDER BY upload_date DESC";

    private static final String INSERT_FILE =
            "INSERT INTO media_files " +
            "(user_id, stored_filename, original_filename, content_type, file_size, file_path, upload_date) " +
            "VALUES (?, ?, ?, ?, ?, ?, NOW())";

    private static final String DELETE_BY_ID =
            "DELETE FROM media_files WHERE id = ? AND user_id = ?";

    // ----------------------------------------------------------------
    // RowMapper
    // ----------------------------------------------------------------

    private static final RowMapper<MediaFile> ROW_MAPPER = (rs, rowNum) -> {
        MediaFile f = new MediaFile();
        f.setId(rs.getLong("id"));
        f.setUserId(rs.getLong("user_id"));
        f.setStoredFilename(rs.getString("stored_filename"));
        f.setOriginalFilename(rs.getString("original_filename"));
        f.setContentType(rs.getString("content_type"));
        f.setFileSize(rs.getLong("file_size"));
        f.setFilePath(rs.getString("file_path"));
        Timestamp ts = rs.getTimestamp("upload_date");
        if (ts != null) {
            f.setUploadDate(ts.toLocalDateTime());
        }
        return f;
    };

    // ----------------------------------------------------------------
    // MediaFileDao implementation
    // ----------------------------------------------------------------

    @Override
    public Optional<MediaFile> findById(long id) throws DaoException {
        try {
            List<MediaFile> rows = jdbc.query(SELECT_BY_ID, ROW_MAPPER, id);
            return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
        } catch (DataAccessException ex) {
            LOGGER.error("findById failed for id={}", id, ex);
            throw new DaoException("Database error in findById", ex);
        }
    }

    @Override
    public List<MediaFile> findByUserId(Long userId) throws DaoException {
        try {
            return jdbc.query(SELECT_BY_USER, ROW_MAPPER, userId);
        } catch (DataAccessException ex) {
            LOGGER.error("findByUserId failed for userId={}", userId, ex);
            throw new DaoException("Database error in findByUserId", ex);
        }
    }

    @Override
    public boolean add(MediaFile file) throws DaoException {
        try {
            KeyHolder keys = new GeneratedKeyHolder();
            int rows = jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        INSERT_FILE, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, file.getUserId());
                ps.setString(2, file.getStoredFilename());
                ps.setString(3, file.getOriginalFilename());
                ps.setString(4, file.getContentType());
                ps.setLong(5, file.getFileSize());
                ps.setString(6, file.getFilePath());
                return ps;
            }, keys);

            if (rows > 0 && keys.getKey() != null) {
                file.setId(keys.getKey().longValue());
            }
            return rows > 0;
        } catch (DataAccessException ex) {
            LOGGER.error("add failed for file={}", file.getOriginalFilename(), ex);
            throw new DaoException("Database error in add", ex);
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
