package blinov_first.service;

import blinov_first.entity.PhoneEntry;
import blinov_first.exception.ServiceException;
import java.util.List;

public interface PhoneEntryService {
    List<PhoneEntry> getUserEntries(Long userId) throws ServiceException;
    boolean addEntry(PhoneEntry entry) throws ServiceException;
    boolean updateEntry(PhoneEntry entry) throws ServiceException;
    boolean deleteEntry(Long entryId, Long userId) throws ServiceException;
}