package blinov_first.service.impl;

import blinov_first.dao.PhoneEntryDao;
import blinov_first.entity.PhoneEntry;
import blinov_first.event.PhoneEntryAddedEvent;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.strategy.PhoneEntrySortStrategy;
import blinov_first.strategy.impl.AlphabeticalSortStrategy;
import blinov_first.strategy.impl.DateAddedSortStrategy;
import blinov_first.strategy.impl.PhoneNumberSortStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PhoneEntryServiceImpl}.
 *
 * Validates business logic, Strategy pattern delegation and Observer event firing.
 */
@ExtendWith(MockitoExtension.class)
class PhoneEntryServiceImplTest {

    @Mock private PhoneEntryDao            entryDao;
    @Mock private ApplicationEventPublisher eventPublisher;

    private PhoneEntrySortStrategy defaultStrategy;
    private Map<String, PhoneEntrySortStrategy> strategyMap;
    private PhoneEntryServiceImpl service;

    @BeforeEach
    void setUp() {
        defaultStrategy = new AlphabeticalSortStrategy();
        strategyMap = Map.of(
                "alphabetical", new AlphabeticalSortStrategy(),
                "phoneNumber",  new PhoneNumberSortStrategy(),
                "dateAdded",    new DateAddedSortStrategy()
        );
        service = new PhoneEntryServiceImpl(
                entryDao, eventPublisher, defaultStrategy, strategyMap);
    }

    // ----------------------------------------------------------------
    // add() — Observer pattern
    // ----------------------------------------------------------------

    @Test
    void add_shouldPublishEvent_whenEntryIsSaved() throws Exception {
        when(entryDao.add(any())).thenReturn(true);
        PhoneEntry entry = makeEntry("Alice", "+1234");

        boolean result = service.add(entry);

        assertTrue(result);
        ArgumentCaptor<PhoneEntryAddedEvent> cap =
                ArgumentCaptor.forClass(PhoneEntryAddedEvent.class);
        verify(eventPublisher).publishEvent(cap.capture());
        assertEquals("Alice", cap.getValue().getEntry().getContactName());
    }

    @Test
    void add_shouldNotPublishEvent_whenDaoReturnsFalse() throws Exception {
        when(entryDao.add(any())).thenReturn(false);

        service.add(makeEntry("Bob", "+5555"));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void add_shouldWrapDaoException() throws Exception {
        when(entryDao.add(any())).thenThrow(new DaoException("insert failed"));

        assertThrows(ServiceException.class,
                () -> service.add(makeEntry("Carl", "+999")));
    }

    // ----------------------------------------------------------------
    // delete()
    // ----------------------------------------------------------------

    @Test
    void delete_shouldReturnTrue_whenEntryBelongsToUser() throws Exception {
        when(entryDao.deleteById(10L, 1L)).thenReturn(true);

        assertTrue(service.delete(10L, 1L));
    }

    @Test
    void delete_shouldReturnFalse_whenEntryNotFound() throws Exception {
        when(entryDao.deleteById(99L, 1L)).thenReturn(false);

        assertFalse(service.delete(99L, 1L));
    }

    // ----------------------------------------------------------------
    // Strategy pattern — sorting
    // ----------------------------------------------------------------

    @Test
    void findByUserId_shouldSortAlphabetically_byDefault() throws Exception {
        List<PhoneEntry> raw = new ArrayList<>(List.of(
                makeEntry("Zara",  "+3"),
                makeEntry("Alice", "+1"),
                makeEntry("Mike",  "+2")
        ));
        when(entryDao.findByUserId(1L)).thenReturn(raw);

        List<PhoneEntry> result = service.findByUserId(1L, null);

        assertEquals("Alice", result.get(0).getContactName());
        assertEquals("Mike",  result.get(1).getContactName());
        assertEquals("Zara",  result.get(2).getContactName());
    }

    @Test
    void findByUserId_shouldSortByPhone_whenStrategyIsPhoneNumber() throws Exception {
        List<PhoneEntry> raw = new ArrayList<>(List.of(
                makeEntry("B", "+300"),
                makeEntry("A", "+100"),
                makeEntry("C", "+200")
        ));
        when(entryDao.findByUserId(1L)).thenReturn(raw);

        List<PhoneEntry> result = service.findByUserId(1L, "phoneNumber");

        assertEquals("+100", result.get(0).getContactPhone());
        assertEquals("+200", result.get(1).getContactPhone());
        assertEquals("+300", result.get(2).getContactPhone());
    }

    @Test
    void findByUserId_shouldFallBackToDefault_whenUnknownStrategyName() throws Exception {
        List<PhoneEntry> raw = new ArrayList<>(List.of(
                makeEntry("Zara",  "+3"),
                makeEntry("Alice", "+1")
        ));
        when(entryDao.findByUserId(1L)).thenReturn(raw);

        List<PhoneEntry> result = service.findByUserId(1L, "nonExistentStrategy");

        assertEquals("Alice", result.get(0).getContactName());
    }

    // ----------------------------------------------------------------
    // update()
    // ----------------------------------------------------------------

    @Test
    void update_shouldReturnTrue_whenDaoSucceeds() throws Exception {
        when(entryDao.update(any())).thenReturn(true);

        assertTrue(service.update(makeEntry("Alice", "+1")));
    }

    @Test
    void update_shouldWrapDaoException() throws Exception {
        when(entryDao.update(any())).thenThrow(new DaoException("error"));

        assertThrows(ServiceException.class,
                () -> service.update(makeEntry("Alice", "+1")));
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private PhoneEntry makeEntry(String name, String phone) {
        PhoneEntry e = new PhoneEntry();
        e.setUserId(1L);
        e.setContactName(name);
        e.setContactPhone(phone);
        return e;
    }
}
