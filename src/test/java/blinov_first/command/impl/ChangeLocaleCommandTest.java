package blinov_first.command.impl;

import blinov_first.util.AttributeName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeLocaleCommandTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private final ChangeLocaleCommand command = new ChangeLocaleCommand();

    @Test
    void execute_shouldSetLangRu_whenParamIsRu() throws Exception {
        when(request.getParameter("lang")).thenReturn("ru");
        when(request.getSession(true)).thenReturn(session);
        when(request.getAttribute(AttributeName.HTTP_RESPONSE)).thenReturn(response);
        when(session.getAttribute(AttributeName.USER_ID)).thenReturn(1L);

        String result = command.execute(request);

        verify(session).setAttribute("lang", "ru");
        assertTrue(result.startsWith("redirect:"));
    }

    @Test
    void execute_shouldDefaultToEn_whenParamIsInvalid() throws Exception {
        when(request.getParameter("lang")).thenReturn("fr");
        when(request.getSession(true)).thenReturn(session);
        when(request.getAttribute(AttributeName.HTTP_RESPONSE)).thenReturn(response);
        when(session.getAttribute(AttributeName.USER_ID)).thenReturn(null);

        command.execute(request);

        verify(session).setAttribute("lang", "en");
    }

    @Test
    void execute_shouldDefaultToEn_whenParamIsNull() throws Exception {
        when(request.getParameter("lang")).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(request.getAttribute(AttributeName.HTTP_RESPONSE)).thenReturn(response);
        when(session.getAttribute(AttributeName.USER_ID)).thenReturn(null);

        command.execute(request);

        verify(session).setAttribute("lang", "en");
    }

    @Test
    void execute_shouldRedirectToMain_whenUserIsLoggedIn() throws Exception {
        when(request.getParameter("lang")).thenReturn("en");
        when(request.getSession(true)).thenReturn(session);
        when(request.getAttribute(AttributeName.HTTP_RESPONSE)).thenReturn(response);
        when(session.getAttribute(AttributeName.USER_ID)).thenReturn(42L);

        String result = command.execute(request);

        assertTrue(result.contains("list_entries") || result.contains("phone_book"));
    }

    @Test
    void execute_shouldRedirectToIndex_whenUserIsNotLoggedIn() throws Exception {
        when(request.getParameter("lang")).thenReturn("en");
        when(request.getSession(true)).thenReturn(session);
        when(request.getAttribute(AttributeName.HTTP_RESPONSE)).thenReturn(response);
        when(session.getAttribute(AttributeName.USER_ID)).thenReturn(null);

        String result = command.execute(request);

        assertTrue(result.contains("index") || result.endsWith("/"));
    }
}
