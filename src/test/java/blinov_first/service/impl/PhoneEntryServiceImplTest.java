package blinov_first.service.impl;

import blinov_first.entity.PhoneEntry;
import blinov_first.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneEntryServiceImplTest {

    private final PhoneEntryServiceImpl service = PhoneEntryServiceImpl.getInstance();

    @Test
    void addEntry_shouldThrowServiceException_whenEntryIsNull() {
        assertThrows(ServiceException.class, () -> service.addEntry(null));
    }

    @Test
    void addEntry_shouldThrowServiceException_whenUserIdIsNull() {
        PhoneEntry entry = new PhoneEntry("Alice", "+1234567890", "alice@test.com", null);
        assertThrows(ServiceException.class, () -> service.addEntry(entry));
    }

    @Test
    void addEntry_shouldThrowServiceException_whenUserIdIsZero() {
        PhoneEntry entry = new PhoneEntry("Alice", "+1234567890", "alice@test.com", 0L);
        assertThrows(ServiceException.class, () -> service.addEntry(entry));
    }

    @Test
    void addEntry_shouldThrowServiceException_whenUserIdIsNegative() {
        PhoneEntry entry = new PhoneEntry("Alice", "+1234567890", "alice@test.com", -1L);
        assertThrows(ServiceException.class, () -> service.addEntry(entry));
    }

    @Test
    void updateEntry_shouldThrowServiceException_whenEntryIsNull() {
        assertThrows(ServiceException.class, () -> service.updateEntry(null));
    }

    @Test
    void updateEntry_shouldThrowServiceException_whenEntryIdIsZero() {
        PhoneEntry entry = new PhoneEntry("Alice", "+111", "a@test.com", 1L);
        entry.setId(0);
        assertThrows(ServiceException.class, () -> service.updateEntry(entry));
    }

    @Test
    void updateEntry_shouldThrowServiceException_whenEntryIdIsNegative() {
        PhoneEntry entry = new PhoneEntry("Alice", "+111", "a@test.com", 1L);
        entry.setId(-5);
        assertThrows(ServiceException.class, () -> service.updateEntry(entry));
    }

    @Test
    void deleteEntry_shouldThrowServiceException_whenEntryIdIsNull() {
        assertThrows(ServiceException.class, () -> service.deleteEntry(null, 1L));
    }

    @Test
    void deleteEntry_shouldThrowServiceException_whenUserIdIsNull() {
        assertThrows(ServiceException.class, () -> service.deleteEntry(1L, null));
    }

    @Test
    void deleteEntry_shouldThrowServiceException_whenEntryIdIsZero() {
        assertThrows(ServiceException.class, () -> service.deleteEntry(0L, 1L));
    }

    @Test
    void deleteEntry_shouldThrowServiceException_whenUserIdIsZero() {
        assertThrows(ServiceException.class, () -> service.deleteEntry(1L, 0L));
    }
}
