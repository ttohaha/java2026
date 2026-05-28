package blinov_first.service.impl;

import blinov_first.dao.PhoneEntryDao;
import blinov_first.entity.PhoneEntry;
import blinov_first.event.PhoneEntryAddedEvent;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.PhoneEntryService;
import blinov_first.strategy.PhoneEntrySortStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link PhoneEntryService}.
 *
 * PATTERN — Singleton:   @Service — one instance per context.
 * PATTERN — Strategy:    delegates sorting to the injected {@link PhoneEntrySortStrategy};
 *                        the concrete strategy is selected at runtime via the
 *                        {@code sort} parameter or falls back to the default.
 * PATTERN — Observer:    publishes {@link PhoneEntryAddedEvent} after a new entry is added.
 */
@Service
public class PhoneEntryServiceImpl implements PhoneEntryService {

    private static final Logger LOGGER = LogManager.getLogger(PhoneEntryServiceImpl.class);

    private static PhoneEntryServiceImpl INSTANCE;

    private final PhoneEntryDao            entryDao;
    private final ApplicationEventPublisher eventPublisher;
    private final PhoneEntrySortStrategy   defaultStrategy;
    private final Map<String, PhoneEntrySortStrategy> strategyMap;

    public PhoneEntryServiceImpl(
            PhoneEntryDao entryDao,
            ApplicationEventPublisher eventPublisher,
            @Qualifier("defaultSortStrategy") PhoneEntrySortStrategy defaultStrategy,
            Map<String, PhoneEntrySortStrategy> sortStrategyMap) {

        this.entryDao        = entryDao;
        this.eventPublisher  = eventPublisher;
        this.defaultStrategy = defaultStrategy;
        this.strategyMap     = sortStrategyMap;
        INSTANCE             = this;
    }

    /** @deprecated Prefer Spring injection. */
    @Deprecated
    public static PhoneEntryServiceImpl getInstance() {
        return INSTANCE;
    }

    // ----------------------------------------------------------------
    // PhoneEntryService implementation
    // ----------------------------------------------------------------

    @Override
    public Optional<PhoneEntry> findById(long id) throws ServiceException {
        try {
            return entryDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error finding entry by id=" + id, e);
        }
    }

    @Override
    public List<PhoneEntry> findByUserId(Long userId, String sort) throws ServiceException {
        try {
            List<PhoneEntry> entries = new ArrayList<>(entryDao.findByUserId(userId));
            resolveStrategy(sort).sort(entries);
            return entries;
        } catch (DaoException e) {
            throw new ServiceException("Error fetching entries for userId=" + userId, e);
        }
    }

    @Override
    public List<PhoneEntry> findByUserIdPaged(Long userId, int page,
                                               int pageSize, String sort)
            throws ServiceException {
        try {
            int offset = (page - 1) * pageSize;
            List<PhoneEntry> entries =
                    new ArrayList<>(entryDao.findByUserIdPaged(userId, offset, pageSize));
            resolveStrategy(sort).sort(entries);
            return entries;
        } catch (DaoException e) {
            throw new ServiceException("Error fetching paged entries for userId=" + userId, e);
        }
    }

    @Override
    public int countByUserId(Long userId) throws ServiceException {
        try {
            return entryDao.countByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException("Error counting entries for userId=" + userId, e);
        }
    }

    @Override
    public List<PhoneEntry> search(Long userId, String query) throws ServiceException {
        try {
            return entryDao.searchByUserIdAndQuery(userId, query);
        } catch (DaoException e) {
            throw new ServiceException("Error searching entries", e);
        }
    }

    @Override
    public boolean add(PhoneEntry entry) throws ServiceException {
        try {
            boolean saved = entryDao.add(entry);
            if (saved) {
                // Observer pattern: fire event so listeners can react
                eventPublisher.publishEvent(new PhoneEntryAddedEvent(this, entry));
                LOGGER.info("Phone entry added: name={}, userId={}",
                        entry.getContactName(), entry.getUserId());
            }
            return saved;
        } catch (DaoException e) {
            throw new ServiceException("Error adding phone entry", e);
        }
    }

    @Override
    public boolean update(PhoneEntry entry) throws ServiceException {
        try {
            return entryDao.update(entry);
        } catch (DaoException e) {
            throw new ServiceException("Error updating phone entry", e);
        }
    }

    @Override
    public boolean delete(long entryId, Long userId) throws ServiceException {
        try {
            return entryDao.deleteById(entryId, userId);
        } catch (DaoException e) {
            throw new ServiceException("Error deleting phone entry id=" + entryId, e);
        }
    }

    // ----------------------------------------------------------------
    // Strategy helper
    // ----------------------------------------------------------------

    /**
     * Resolves the concrete sort strategy by name, falling back to the default
     * configured via {@code app.phonebook.default-sort}.
     */
    private PhoneEntrySortStrategy resolveStrategy(String sort) {
        if (sort == null || sort.isBlank()) {
            return defaultStrategy;
        }
        PhoneEntrySortStrategy strategy = strategyMap.get(sort);
        if (strategy == null) {
            LOGGER.warn("Unknown sort strategy '{}', using default", sort);
            return defaultStrategy;
        }
        return strategy;
    }
}
