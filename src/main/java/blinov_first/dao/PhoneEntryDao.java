package blinov_first.dao;

import blinov_first.entity.PhoneEntry;
import blinov_first.exception.DaoException;

import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for {@link PhoneEntry} entities.
 */
public interface PhoneEntryDao {

    Optional<PhoneEntry> findById(long id) throws DaoException;

    List<PhoneEntry> findByUserId(Long userId) throws DaoException;

    List<PhoneEntry> findByUserIdPaged(Long userId, int offset, int limit) throws DaoException;

    int countByUserId(Long userId) throws DaoException;

    List<PhoneEntry> searchByUserIdAndQuery(Long userId, String query) throws DaoException;

    boolean add(PhoneEntry entry) throws DaoException;

    boolean update(PhoneEntry entry) throws DaoException;

    boolean deleteById(long id, Long userId) throws DaoException;
}
