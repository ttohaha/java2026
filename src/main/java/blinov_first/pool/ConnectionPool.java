package blinov_first.pool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionPool.class);

    private static volatile ConnectionPool instance;

    private static volatile int configuredMinSize = PoolConfig.MIN_SIZE;
    private static volatile int configuredMaxSize = PoolConfig.MAX_SIZE;

    private final BlockingQueue<Connection> availableQueue;
    private final AtomicInteger currentSize;
    private final int maxSize;
    private final int timeoutMs;

    private ConnectionPool() {
        this.availableQueue = new LinkedBlockingQueue<>();
        this.currentSize    = new AtomicInteger(0);
        this.maxSize        = configuredMaxSize;
        this.timeoutMs      = PoolConfig.TIMEOUT_MS;
        initializePool();
    }

    /**
     * Must be called before the first getInstance() call.
     * Typically invoked from ConnectionPoolListener on application startup.
     */
    public static void configure(int minSize, int maxSize) {
        if (instance != null) {
            LOGGER.warn("configure() called after pool was already created — ignored");
            return;
        }
        configuredMinSize = minSize;
        configuredMaxSize = maxSize;
        LOGGER.info("ConnectionPool configured: minSize={}, maxSize={}", minSize, maxSize);
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    private void initializePool() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("MySQL driver not found", e);
            throw new RuntimeException("Database driver initialization failed", e);
        }

        for (int i = 0; i < configuredMinSize; i++) {
            try {
                Connection real = DriverManager.getConnection(
                        PoolConfig.DB_URL, PoolConfig.DB_USER, PoolConfig.DB_PASSWORD);
                availableQueue.offer(createProxyConnection(real));
                currentSize.incrementAndGet();
            } catch (SQLException e) {
                LOGGER.error("Failed to create initial connection #{}", i, e);
            }
        }

        LOGGER.info("ConnectionPool initialized — min: {}, max: {}, available: {}",
                configuredMinSize, maxSize, availableQueue.size());
    }

    public Connection getConnection() throws SQLException {
        Connection proxy;
        try {
            proxy = availableQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Connection pool wait interrupted", e);
        }

        if (proxy == null) {
            if (currentSize.get() < maxSize) {
                synchronized (this) {
                    if (currentSize.get() < maxSize) {
                        Connection real = DriverManager.getConnection(
                                PoolConfig.DB_URL, PoolConfig.DB_USER, PoolConfig.DB_PASSWORD);
                        currentSize.incrementAndGet();
                        LOGGER.debug("New connection created. Total: {}", currentSize.get());
                        return createProxyConnection(real);
                    }
                }
            }
            throw new SQLException(
                    "No available connections within " + timeoutMs + " ms");
        }

        if (!proxy.isValid(PoolConfig.VALIDATION_TIMEOUT_SEC)) {
            LOGGER.warn("Discarded dead connection, creating replacement");
            currentSize.decrementAndGet();
            Connection real = DriverManager.getConnection(
                    PoolConfig.DB_URL, PoolConfig.DB_USER, PoolConfig.DB_PASSWORD);
            return createProxyConnection(real);
        }

        LOGGER.debug("Connection issued. Available: {}", availableQueue.size());
        return proxy;
    }

    public void releaseConnection(Connection proxy) {
        if (proxy == null) return;
        try {
            if (proxy.isValid(PoolConfig.VALIDATION_TIMEOUT_SEC) && !proxy.isClosed()) {
                proxy.setAutoCommit(true);
                availableQueue.offer(proxy);
                LOGGER.debug("Connection returned. Available: {}", availableQueue.size());
            } else {
                LOGGER.warn("Discarded invalid connection on release");
                currentSize.decrementAndGet();
            }
        } catch (SQLException e) {
            LOGGER.error("Error releasing connection", e);
            currentSize.decrementAndGet();
        }
    }

    public int getAvailableCount() {
        return availableQueue.size();
    }

    public int getTotalCount() {
        return currentSize.get();
    }

    private Connection createProxyConnection(Connection real) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        releaseConnection((Connection) proxy);
                        return null;
                    }
                    return method.invoke(real, args);
                }
        );
    }

    public void shutdown() {
        LOGGER.info("Shutting down ConnectionPool...");
        Connection conn;
        while ((conn = availableQueue.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("Error closing connection during shutdown", e);
            }
        }
        availableQueue.clear();
        currentSize.set(0);
        LOGGER.info("ConnectionPool shut down. Connections closed.");
    }
}
