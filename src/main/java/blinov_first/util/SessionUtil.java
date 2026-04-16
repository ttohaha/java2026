package blinov_first.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public static Long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.debug("getSession(false) returned null");
            return null;
        }
        Object userIdObj = session.getAttribute(AttributeName.USER_ID);
        LOGGER.debug("Raw userId from session: {} (type: {})", userIdObj, userIdObj != null ? userIdObj.getClass().getName() : "null");

        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                LOGGER.warn("Failed to parse user id from session: {}", userIdObj);
                return null;
            }
        }
        return null;
    }

    public static String getLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object loginObj = session.getAttribute(AttributeName.LOGIN);
        return (loginObj instanceof String) ? (String) loginObj : null;
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            LOGGER.info("Session invalidated for user");
        }
    }
}