package blinov_first.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class AppSessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LogManager.getLogger(AppSessionListener.class);

    private static final String ATTRIBUTE_ACTIVE_SESSIONS = "activeSessions";

    private static final AtomicInteger activeSessionCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        int count = activeSessionCount.incrementAndGet();
        HttpSession session = event.getSession();
        session.getServletContext().setAttribute(ATTRIBUTE_ACTIVE_SESSIONS, count);
        LOGGER.debug("Session created: id={}. Active sessions: {}", session.getId(), count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        int count = activeSessionCount.decrementAndGet();
        HttpSession session = event.getSession();
        session.getServletContext().setAttribute(ATTRIBUTE_ACTIVE_SESSIONS, count);
        LOGGER.debug("Session destroyed: id={}. Active sessions: {}", session.getId(), count);
    }

    public static int getActiveSessionCount() {
        return activeSessionCount.get();
    }
}
