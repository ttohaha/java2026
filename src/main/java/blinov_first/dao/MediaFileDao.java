package blinov_first.dao;

import blinov_first.entity.MediaFile;
import blinov_first.exception.DaoException;
import java.util.List;
import java.util.Optional;

public interface MediaFileDao {
    Optional<MediaFile> findById(int id) throws DaoException;
    List<MediaFile> findByUserId(Long userId) throws DaoException;
    boolean add(MediaFile file) throws DaoException;
    boolean deleteById(int id, Long userId) throws DaoException;
}