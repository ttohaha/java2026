package blinov_first.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class SessionUtil {

    private static final Logger LOGGER = LogManager.getLogger(SessionUtil.class);

    private SessionUtil() {}

    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(AttributeName.USER_ID) != null;
        LOGGER.debug("Session check: session={}, userId={}",
                session != null ? "exists" : "null",
                session != null ? session.getAttribute(AttributeName.USER_ID) : "N/A");
        return loggedIn;
    }

    public static Optional<Long> getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.debug("getSession(false) returned null");
            return Optional.empty();
        }
        Object raw = session.getAttribute(AttributeName.USER_ID);
        LOGGER.debug("Raw userId from session: {} (type: {})",
                raw, raw != null ? raw.getClass().getName() : "null");

        if (raw instanceof Long) {
            return Optional.of((Long) raw);
        }
        if (raw instanceof Integer) {
            return Optional.of(((Integer) raw).longValue());
        }
        if (raw instanceof String) {
            try {
                return Optional.of(Long.parseLong((String) raw));
            } catch (NumberFormatException e) {
                LOGGER.warn("Failed to parse userId from session: {}", raw);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object raw = session.getAttribute(AttributeName.LOGIN);
        if (raw instanceof String) {
            return Optional.of((String) raw);
        }
        return Optional.empty();
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            LOGGER.info("Session invalidated for user");
        }
    }
}
