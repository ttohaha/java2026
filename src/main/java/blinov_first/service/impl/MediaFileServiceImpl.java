package blinov_first.service.impl;

import blinov_first.config.UploadConfig;
import blinov_first.dao.MediaFileDao;
import blinov_first.dao.impl.MediaFileDaoImpl;
import blinov_first.entity.MediaFile;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.MediaFileService;
import blinov_first.util.FileNameGenerator;
import blinov_first.util.FileValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MediaFileServiceImpl implements MediaFileService {

    private static final Logger LOGGER = LogManager.getLogger(MediaFileServiceImpl.class);
    private static final MediaFileServiceImpl INSTANCE = new MediaFileServiceImpl();

    private MediaFileServiceImpl() {}

    public static MediaFileServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<MediaFile> getUserFiles(Long userId) throws ServiceException {
        try {
            return MediaFileDaoImpl.getInstance().findByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to fetch user files", e);
        }
    }

    @Override
    public boolean uploadFile(InputStream fileStream, String originalFilename, String contentType, long fileSize, Long userId) throws ServiceException {
        if (!FileValidator.isAllowedType(originalFilename, contentType)) {
            throw new ServiceException("File type is not allowed");
        }
        if (!FileValidator.isWithinSizeLimit(fileSize)) {
            throw new ServiceException("File size exceeds maximum limit");
        }

        String safeName = FileValidator.sanitizeFilename(originalFilename);
        String storedName = FileNameGenerator.generateSafeName(safeName);
        Path targetDir = Paths.get(UploadConfig.UPLOAD_DIR);
        Path targetPath = targetDir.resolve(storedName);

        UploadConfig.ensureUploadDirectoryExists();

        try {
            Files.copy(fileStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            MediaFile fileRecord = new MediaFile(userId, storedName, safeName, contentType, fileSize, targetPath.toString());
            return MediaFileDaoImpl.getInstance().add(fileRecord);
        } catch (IOException e) {
            LOGGER.error("Failed to save file to disk: {}", targetPath, e);
            throw new ServiceException("File storage failed", e);
        } catch (DaoException e) {
            try { Files.deleteIfExists(targetPath); } catch (IOException ignored) {}
            LOGGER.error("Database error during file upload", e);
            throw new ServiceException("Database error during file upload", e);
        }
    }

    @Override
    public boolean deleteFile(int fileId, Long userId) throws ServiceException {
        try {
            MediaFileDao dao = MediaFileDaoImpl.getInstance();
            var fileOpt = dao.findById(fileId);
            if (fileOpt.isEmpty()) return false;
            MediaFile file = fileOpt.get();
            if (!file.getUserId().equals(userId)) {
                LOGGER.warn("Unauthorized delete attempt for file: {}", fileId);
                return false;
            }
            boolean dbDeleted = dao.deleteById(fileId, userId);
            if (dbDeleted) {
                try { Files.deleteIfExists(Paths.get(file.getFilePath())); }
                catch (IOException e) { LOGGER.warn("Failed to delete physical file: {}", file.getFilePath(), e); }
            }
            return dbDeleted;
        } catch (DaoException e) {
            throw new ServiceException("File deletion failed", e);
        }
    }

    @Override
    public MediaFile getFileForDownload(int fileId, Long userId) throws ServiceException {
        try {
            var fileOpt = MediaFileDaoImpl.getInstance().findById(fileId);
            if (fileOpt.isEmpty()) return null;
            MediaFile file = fileOpt.get();
            if (!file.getUserId().equals(userId)) {
                LOGGER.warn("Unauthorized download attempt for file: {}", fileId);
                return null;
            }
            return file;
        } catch (DaoException e) {
            throw new ServiceException("Failed to retrieve file metadata", e);
        }
    }
}