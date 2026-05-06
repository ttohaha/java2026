package blinov_first.service.impl;

import blinov_first.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private final UserServiceImpl service = UserServiceImpl.getInstance();

    @Test
    void authenticate_shouldReturnFalse_whenLoginIsNull() throws ServiceException {
        assertFalse(service.authenticate(null, "secret123"));
    }

    @Test
    void authenticate_shouldReturnFalse_whenPasswordIsNull() throws ServiceException {
        assertFalse(service.authenticate("john", null));
    }

    @Test
    void authenticate_shouldReturnFalse_whenBothNull() throws ServiceException {
        assertFalse(service.authenticate(null, null));
    }

    @Test
    void registerWithConfirmation_shouldReturnFalse_whenLoginTooShort() throws ServiceException {
        assertFalse(service.registerWithConfirmation("ab", "secret123", "test@test.com"));
    }

    @Test
    void registerWithConfirmation_shouldReturnFalse_whenPasswordTooShort() throws ServiceException {
        assertFalse(service.registerWithConfirmation("validlogin", "123", "test@test.com"));
    }

    @Test
    void registerWithConfirmation_shouldReturnFalse_whenEmailIsNull() throws ServiceException {
        assertFalse(service.registerWithConfirmation("validlogin", "secret123", null));
    }

    @Test
    void registerWithConfirmation_shouldReturnFalse_whenEmailIsEmpty() throws ServiceException {
        assertFalse(service.registerWithConfirmation("validlogin", "secret123", ""));
    }

    @Test
    void registerWithConfirmation_shouldReturnFalse_whenLoginIsNull() throws ServiceException {
        assertFalse(service.registerWithConfirmation(null, "secret123", "test@test.com"));
    }

    @Test
    void confirmRegistration_shouldReturnFalse_whenTokenIsNull() throws ServiceException {
        assertFalse(service.confirmRegistration(null));
    }

    @Test
    void confirmRegistration_shouldReturnFalse_whenTokenIsEmpty() throws ServiceException {
        assertFalse(service.confirmRegistration(""));
    }
}
