package blinov_first.service;

import blinov_first.entity.MediaFile;
import blinov_first.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MediaFileService {

    List<MediaFile> findByUserId(Long userId) throws ServiceException;

    Optional<MediaFile> findById(long id) throws ServiceException;

    /**
     * Stores the uploaded file on disk and saves metadata to the database.
     *
     * @return the persisted {@link MediaFile} with a generated ID
     */
    MediaFile upload(MultipartFile multipartFile, Long userId) throws ServiceException;

    boolean delete(long fileId, Long userId) throws ServiceException;
}
