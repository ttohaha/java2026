package blinov_first.service.impl;

import blinov_first.dao.PhoneEntryDao;
import blinov_first.dao.impl.PhoneEntryDaoImpl;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.PhoneEntryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class PhoneEntryServiceImpl implements PhoneEntryService {

    private static final Logger LOGGER = LogManager.getLogger(PhoneEntryServiceImpl.class);
    private static final PhoneEntryServiceImpl INSTANCE = new PhoneEntryServiceImpl();

    private PhoneEntryServiceImpl() {}

    public static PhoneEntryServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<PhoneEntry> getUserEntries(Long userId) throws ServiceException {
        try {
            return PhoneEntryDaoImpl.getInstance().findByUserId(userId);
        } catch (DaoException e) {
            LOGGER.error("Failed to fetch entries for user: {}", userId, e);
            throw new ServiceException("Failed to retrieve phone entries", e);
        }
    }

    @Override
    public boolean addEntry(PhoneEntry entry) throws ServiceException {
        if (entry == null || entry.getUserId() == null || entry.getUserId() <= 0) {
            throw new ServiceException("Invalid entry data: userId is required");
        }
        try {
            return PhoneEntryDaoImpl.getInstance().add(entry);
        } catch (DaoException e) {
            LOGGER.error("Failed to add entry for user: {}", entry.getUserId(), e);
            throw new ServiceException("Failed to add phone entry", e);
        }
    }

    @Override
    public boolean updateEntry(PhoneEntry entry) throws ServiceException {
        if (entry == null || entry.getId() <= 0) {
            throw new ServiceException("Invalid entry data: id is required");
        }
        try {
            return PhoneEntryDaoImpl.getInstance().update(entry);
        } catch (DaoException e) {
            LOGGER.error("Failed to update entry id: {}", entry.getId(), e);
            throw new ServiceException("Failed to update phone entry", e);
        }
    }

    @Override
    public boolean deleteEntry(Long entryId, Long userId) throws ServiceException {
        if (entryId == null || entryId <= 0 || userId == null || userId <= 0) {
            throw new ServiceException("Invalid parameters for deletion");
        }
        try {
            PhoneEntryDao dao = PhoneEntryDaoImpl.getInstance();
            Optional<PhoneEntry> entryOpt = dao.findById(entryId.intValue());
            if (entryOpt.isEmpty()) {
                LOGGER.warn("Entry not found for deletion: {}", entryId);
                return false;
            }
            PhoneEntry entry = entryOpt.get();
            if (!entry.getUserId().equals(userId)) {
                LOGGER.warn("Unauthorized deletion attempt: user {} tried to delete entry {} owned by user {}",
                        userId, entryId, entry.getUserId());
                return false;
            }
            return dao.deleteById(entryId.intValue());
        } catch (DaoException e) {
            LOGGER.error("Failed to delete entry: {}", entryId, e);
            throw new ServiceException("Failed to delete phone entry", e);
        }
    }
}