package blinov_first.pool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);

    // Added timezone parameters to avoid common MySQL connection errors
    private static final String URL = "jdbc:mysql://localhost:3306/phonestest2?useUnicode=true&serverTimezone=UTC";
    private static final int POOL_SIZE = 8;

    // 1. STATIC BLOCK MUST BE HERE (Before instance creation)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL Driver registered successfully");
        } catch (ClassNotFoundException e) {
            logger.fatal("MySQL Driver not found in classpath", e);
            throw new RuntimeException(e);
        }
    }

    // 2. NOW IT IS SAFE TO CREATE THE INSTANCE
    private static final ConnectionPool instance = new ConnectionPool();

    private final BlockingQueue<Connection> free = new LinkedBlockingQueue<>(POOL_SIZE);
    private final BlockingQueue<Connection> used = new LinkedBlockingQueue<>(POOL_SIZE);

    private ConnectionPool() {
        Properties prop = new Properties();
        prop.put("user", "root");
        prop.put("password", "7GAZcCrhHaLH");

        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection connection = DriverManager.getConnection(URL, prop);
                free.add(connection);
            } catch (SQLException e) {
                logger.error("Failed to create connection #{}", i + 1, e);
            }
        }
        logger.info("Pool initialized with {} connections", free.size());
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = free.take();
            used.put(connection);
            logger.debug("Connection issued. Used: {}, Free: {}", used.size(), free.size());
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for connection", e);
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                used.remove(connection);
                free.put(connection);
                logger.debug("Connection returned. Used: {}, Free: {}", used.size(), free.size());
            } catch (InterruptedException e) {
                logger.error("Interrupted while releasing connection", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void destroyPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection connection = free.take();
                connection.close();
            } catch (SQLException | InterruptedException e) {
                logger.error("Error closing connection during pool destruction", e);
            }
        }
        logger.info("Connection pool destroyed successfully");
    }
}