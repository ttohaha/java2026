package blinov_first.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashMessageTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Test
    void success_shouldStoreMessageInSession() {
        when(request.getSession(true)).thenReturn(session);

        FlashMessage.success(request, "Operation successful");

        verify(session).setAttribute("flashSuccess", "Operation successful");
    }

    @Test
    void error_shouldStoreMessageInSession() {
        when(request.getSession(true)).thenReturn(session);

        FlashMessage.error(request, "Something went wrong");

        verify(session).setAttribute("flashError", "Something went wrong");
    }

    @Test
    void transfer_shouldMoveSuccessToRequestAndRemoveFromSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("flashSuccess")).thenReturn("Done!");
        when(session.getAttribute("flashError")).thenReturn(null);

        FlashMessage.transfer(request);

        verify(request).setAttribute(AttributeName.SUCCESS_MSG, "Done!");
        verify(session).removeAttribute("flashSuccess");
        verify(session, never()).removeAttribute("flashError");
    }

    @Test
    void transfer_shouldMoveErrorToRequestAndRemoveFromSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("flashSuccess")).thenReturn(null);
        when(session.getAttribute("flashError")).thenReturn("Error occurred");

        FlashMessage.transfer(request);

        verify(request).setAttribute(AttributeName.ERROR_MSG, "Error occurred");
        verify(session).removeAttribute("flashError");
        verify(session, never()).removeAttribute("flashSuccess");
    }

    @Test
    void transfer_shouldDoNothing_whenSessionIsNull() {
        when(request.getSession(false)).thenReturn(null);

        FlashMessage.transfer(request);

        verify(request, never()).setAttribute(any(), any());
    }

    @Test
    void transfer_shouldDoNothing_whenNoFlashMessages() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("flashSuccess")).thenReturn(null);
        when(session.getAttribute("flashError")).thenReturn(null);

        FlashMessage.transfer(request);

        verify(request, never()).setAttribute(any(), any());
    }
}
