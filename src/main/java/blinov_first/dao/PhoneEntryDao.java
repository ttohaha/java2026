package blinov_first.dao;

import blinov_first.entity.PhoneEntry;
import blinov_first.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface PhoneEntryDao {

    boolean add(PhoneEntry entry) throws DaoException;
    Optional<PhoneEntry> findById(int id) throws DaoException;
    List<PhoneEntry> findByUserId(Long userId) throws DaoException;
    List<PhoneEntry> findByUserIdPaged(Long userId, int offset, int limit) throws DaoException;
    List<PhoneEntry> searchByUserIdAndQuery(Long userId, String query, int limit) throws DaoException;
    int countByUserId(Long userId) throws DaoException;
    boolean update(PhoneEntry entry) throws DaoException;
    boolean deleteById(int id) throws DaoException;
    boolean deleteAllByUserId(Long userId) throws DaoException;
}
