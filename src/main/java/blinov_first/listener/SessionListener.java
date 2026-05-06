package blinov_first.listener;

import blinov_first.util.AttributeName;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LogManager.getLogger(SessionListener.class);

    private static final String CONTEXT_ATTR_ACTIVE_SESSIONS = "activeSessions";
    private static final AtomicInteger activeSessionCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        int count = activeSessionCount.incrementAndGet();
        ServletContext context = event.getSession().getServletContext();
        context.setAttribute(CONTEXT_ATTR_ACTIVE_SESSIONS, count);
        LOGGER.debug("Session created. Active sessions: {}", count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        String login = (String) session.getAttribute(AttributeName.LOGIN);
        if (login != null) {
            LOGGER.info("Session expired for user: '{}'", login);
        }

        int count = activeSessionCount.decrementAndGet();
        ServletContext context = session.getServletContext();
        context.setAttribute(CONTEXT_ATTR_ACTIVE_SESSIONS, count);
        LOGGER.debug("Session destroyed. Active sessions: {}", count);
    }
}
