package blinov_first.pool;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class ConnectionPoolListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionPoolListener.class);

    private static final String PARAM_MIN_SIZE = "pool.min.size";
    private static final String PARAM_MAX_SIZE = "pool.max.size";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        int minSize = parseIntParam(context, PARAM_MIN_SIZE, PoolConfig.MIN_SIZE);
        int maxSize = parseIntParam(context, PARAM_MAX_SIZE, PoolConfig.MAX_SIZE);

        ConnectionPool.configure(minSize, maxSize);

        ConnectionPool pool = ConnectionPool.getInstance();

        context.setAttribute("connectionPool", pool);

        LOGGER.info("Application started. Pool ready — available: {}/{}",
                pool.getAvailableCount(), pool.getTotalCount());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application shutting down. Closing pool connections...");
        try {
            ConnectionPool.getInstance().shutdown();
        } catch (Exception e) {
            LOGGER.error("Error during pool shutdown", e);
        }
    }

    private int parseIntParam(ServletContext ctx, String name, int defaultValue) {
        String raw = ctx.getInitParameter(name);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid context-param '{}': '{}', using default {}", name, raw, defaultValue);
            return defaultValue;
        }
    }
}
