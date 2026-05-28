package blinov_first.service.impl;

import blinov_first.config.UploadProperties;
import blinov_first.dao.MediaFileDao;
import blinov_first.entity.MediaFile;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.MediaFileService;
import blinov_first.util.FileNameGenerator;
import blinov_first.util.FileValidator;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Handles file upload/download on behalf of users.
 * Replaces the old {@code MediaFileServiceImpl} + {@code MediaFileServiceFactory}.
 *
 * PATTERN — Singleton: @Service — one instance per Spring context.
 */
@Service
public class MediaFileServiceImpl implements MediaFileService {

    private static final Logger LOGGER = LogManager.getLogger(MediaFileServiceImpl.class);

    private final MediaFileDao      fileDao;
    private final UploadProperties  props;
    private final FileValidator     fileValidator;

    public MediaFileServiceImpl(MediaFileDao fileDao,
                                UploadProperties props,
                                FileValidator fileValidator) {
        this.fileDao       = fileDao;
        this.props         = props;
        this.fileValidator = fileValidator;
    }

    /** Creates the upload directory on startup if it does not yet exist. */
    @PostConstruct
    public void ensureUploadDir() {
        try {
            Path dir = Paths.get(props.getDir());
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                LOGGER.info("Upload directory created: {}", dir.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Could not create upload directory: {}", props.getDir(), e);
        }
    }

    // ----------------------------------------------------------------
    // MediaFileService implementation
    // ----------------------------------------------------------------

    @Override
    public List<MediaFile> findByUserId(Long userId) throws ServiceException {
        try {
            return fileDao.findByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException("Error fetching files for userId=" + userId, e);
        }
    }

    @Override
    public Optional<MediaFile> findById(long id) throws ServiceException {
        try {
            return fileDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error finding file id=" + id, e);
        }
    }

    @Override
    public MediaFile upload(MultipartFile multipartFile, Long userId) throws ServiceException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ServiceException("Uploaded file must not be empty");
        }

        String originalName  = multipartFile.getOriginalFilename();
        String storedName    = FileNameGenerator.generate(originalName);
        Path   storedPath    = Paths.get(props.getDir(), storedName);

        try {
            Files.copy(multipartFile.getInputStream(), storedPath);
        } catch (IOException e) {
            throw new ServiceException("Failed to save file to disk", e);
        }

        MediaFile file = new MediaFile();
        file.setUserId(userId);
        file.setOriginalFilename(originalName);
        file.setStoredFilename(storedName);
        file.setContentType(multipartFile.getContentType());
        file.setFileSize(multipartFile.getSize());
        file.setFilePath(storedPath.toString());

        try {
            boolean saved = fileDao.add(file);
            if (!saved) {
                deleteFromDisk(storedPath);
                throw new ServiceException("Database insert failed for file: " + originalName);
            }
        } catch (DaoException e) {
            deleteFromDisk(storedPath);
            throw new ServiceException("Error saving file metadata", e);
        }

        LOGGER.info("File uploaded: original='{}', stored='{}', userId={}",
                originalName, storedName, userId);
        return file;
    }

    @Override
    public boolean delete(long fileId, Long userId) throws ServiceException {
        try {
            Optional<MediaFile> fileOpt = fileDao.findById(fileId);
            if (fileOpt.isEmpty()) {
                return false;
            }
            MediaFile file = fileOpt.get();
            if (!file.getUserId().equals(userId)) {
                LOGGER.warn("Delete denied: userId={} attempted to delete fileId={} owned by {}",
                        userId, fileId, file.getUserId());
                return false;
            }

            boolean deleted = fileDao.deleteById(fileId, userId);
            if (deleted) {
                deleteFromDisk(Paths.get(file.getFilePath()));
                LOGGER.info("File deleted: id={}, userId={}", fileId, userId);
            }
            return deleted;
        } catch (DaoException e) {
            throw new ServiceException("Error deleting file id=" + fileId, e);
        }
    }

    // ----------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------

    private void deleteFromDisk(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOGGER.warn("Could not delete file from disk: {}", path, e);
        }
    }
}
