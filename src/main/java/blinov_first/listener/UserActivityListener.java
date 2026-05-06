package blinov_first.listener;

import blinov_first.util.AttributeName;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class UserActivityListener implements HttpSessionAttributeListener {

    private static final Logger LOGGER = LogManager.getLogger(UserActivityListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (AttributeName.USER_ID.equals(event.getName())) {
            String login = (String) event.getSession().getAttribute(AttributeName.LOGIN);
            LOGGER.info("User logged in: login='{}', userId={}, sessionId={}",
                    login, event.getValue(), event.getSession().getId());
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (AttributeName.USER_ID.equals(event.getName())) {
            String login = (String) event.getSession().getAttribute(AttributeName.LOGIN);
            LOGGER.info("User logged out: login='{}', sessionId={}",
                    login, event.getSession().getId());
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (AttributeName.USER_ID.equals(event.getName())) {
            LOGGER.info("Session attribute replaced: name='{}', sessionId={}",
                    event.getName(), event.getSession().getId());
        }
    }
}
