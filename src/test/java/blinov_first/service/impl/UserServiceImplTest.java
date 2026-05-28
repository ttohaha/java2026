package blinov_first.service.impl;

import blinov_first.dao.UserDao;
import blinov_first.entity.User;
import blinov_first.event.UserRegisteredEvent;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * Uses Mockito to isolate the service from the DAO layer and the event publisher.
 * No Spring context is started — tests run fast.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userDao, eventPublisher);
    }

    // ----------------------------------------------------------------
    // register()
    // ----------------------------------------------------------------

    @Test
    void register_shouldReturnFalse_whenLoginAlreadyExists() throws Exception {
        when(userDao.findByLogin("alice")).thenReturn(Optional.of(new User()));

        boolean result = service.register("alice", "password1", "a@a.com", "");

        assertFalse(result);
        verify(userDao, never()).add(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void register_shouldReturnTrue_andPublishEvent_whenLoginIsNew() throws Exception {
        when(userDao.findByLogin("bob")).thenReturn(Optional.empty());
        when(userDao.add(any(User.class))).thenReturn(true);

        boolean result = service.register("bob", "password1", "b@b.com", "123");

        assertTrue(result);
        verify(userDao).add(any(User.class));

        ArgumentCaptor<UserRegisteredEvent> captor =
                ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals("bob", captor.getValue().getUser().getLogin());
        assertNotNull(captor.getValue().getConfirmationToken());
    }

    @Test
    void register_shouldNotPublishEvent_whenDaoAddReturnsFalse() throws Exception {
        when(userDao.findByLogin("carol")).thenReturn(Optional.empty());
        when(userDao.add(any())).thenReturn(false);

        boolean result = service.register("carol", "pass", "c@c.com", "");

        assertFalse(result);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void register_shouldWrapDaoException_asServiceException() throws Exception {
        when(userDao.findByLogin(anyString()))
                .thenThrow(new DaoException("DB down"));

        assertThrows(ServiceException.class,
                () -> service.register("user", "pass", "u@u.com", ""));
    }

    // ----------------------------------------------------------------
    // confirmRegistration()
    // ----------------------------------------------------------------

    @Test
    void confirmRegistration_shouldReturnTrue_whenTokenMatches() throws Exception {
        when(userDao.activateByToken("valid-token")).thenReturn(true);

        assertTrue(service.confirmRegistration("valid-token"));
    }

    @Test
    void confirmRegistration_shouldReturnFalse_whenTokenNotFound() throws Exception {
        when(userDao.activateByToken("bad-token")).thenReturn(false);

        assertFalse(service.confirmRegistration("bad-token"));
    }

    @Test
    void confirmRegistration_shouldWrapDaoException() throws Exception {
        when(userDao.activateByToken(anyString()))
                .thenThrow(new DaoException("error"));

        assertThrows(ServiceException.class,
                () -> service.confirmRegistration("token"));
    }

    // ----------------------------------------------------------------
    // findById()
    // ----------------------------------------------------------------

    @Test
    void findById_shouldReturnUser_whenFound() throws Exception {
        User user = new User();
        user.setId(7L);
        when(userDao.findById(7L)).thenReturn(Optional.of(user));

        Optional<User> result = service.findById(7L);

        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() throws Exception {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.findById(99L).isEmpty());
    }

    // ----------------------------------------------------------------
    // TokenGenerator sanity checks
    // ----------------------------------------------------------------

    @Test
    void tokenGenerator_shouldProduceNonNullToken() {
        assertNotNull(TokenGenerator.generate());
    }

    @Test
    void tokenGenerator_shouldProduceUniqueTokens() {
        assertNotEquals(TokenGenerator.generate(), TokenGenerator.generate());
    }

    @Test
    void tokenGenerator_shouldProduce32CharHexString() {
        String token = TokenGenerator.generate();
        assertEquals(32, token.length());
        assertTrue(token.matches("[a-f0-9]+"),
                "Token should be a lowercase hex string");
    }
}
