package blinov_first.pool;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class ConnectionPoolListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ConnectionPoolListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionPool.getInstance();
        logger.info("ServletContext initialized: Connection pool is ready");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionPool.getInstance().destroyPool();
        logger.info("ServletContext destroyed: Connection pool closed");
    }
}