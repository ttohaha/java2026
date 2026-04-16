package blinov_first.service;

import blinov_first.entity.MediaFile;
import blinov_first.exception.ServiceException;
import java.io.InputStream;
import java.util.List;

public interface MediaFileService {
    List<MediaFile> getUserFiles(Long userId) throws ServiceException;
    boolean uploadFile(InputStream fileStream, String originalFilename, String contentType, long fileSize, Long userId) throws ServiceException;
    boolean deleteFile(int fileId, Long userId) throws ServiceException;
    MediaFile getFileForDownload(int fileId, Long userId) throws ServiceException;
}