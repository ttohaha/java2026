package blinov_first.dao;

import blinov_first.entity.MediaFile;
import blinov_first.exception.DaoException;

import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for {@link MediaFile} entities.
 */
public interface MediaFileDao {

    Optional<MediaFile> findById(long id) throws DaoException;

    List<MediaFile> findByUserId(Long userId) throws DaoException;

    boolean add(MediaFile file) throws DaoException;

    boolean deleteById(long id, Long userId) throws DaoException;
}
