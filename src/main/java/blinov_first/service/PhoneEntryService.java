package blinov_first.service;

import blinov_first.entity.PhoneEntry;
import blinov_first.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface PhoneEntryService {

    Optional<PhoneEntry> findById(long id) throws ServiceException;

    /**
     * Returns all phone entries for the user, sorted using the active
     * {@code PhoneEntrySortStrategy} (Strategy pattern).
     *
     * @param sort optional sort key: "alphabetical" | "phoneNumber" | "dateAdded"
     *             — if null the configured default strategy is used
     */
    List<PhoneEntry> findByUserId(Long userId, String sort) throws ServiceException;

    List<PhoneEntry> findByUserIdPaged(Long userId, int page,
                                       int pageSize, String sort) throws ServiceException;

    int countByUserId(Long userId) throws ServiceException;

    List<PhoneEntry> search(Long userId, String query) throws ServiceException;

    /**
     * Adds an entry and publishes a {@code PhoneEntryAddedEvent} (Observer pattern).
     */
    boolean add(PhoneEntry entry) throws ServiceException;

    boolean update(PhoneEntry entry) throws ServiceException;

    boolean delete(long entryId, Long userId) throws ServiceException;
}
