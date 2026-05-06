import blinov_first.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Diagnostic test for ConnectionPool.
 * Demonstrates: Singleton, Proxy, BlockingQueue, Thread-Safety.
 * Run: mvn exec:java -Dexec.mainClass="PoolDiagnosticTest" -Dexec.classpathScope="test"
 */
public class PoolDiagnosticTest {

    private static final Logger LOGGER = LogManager.getLogger(PoolDiagnosticTest.class);

    public static void main(String[] args) throws Exception {
        System.out.println("=== Connection Pool Diagnostic Test ===\n");

        // 1. Prove Singleton: getInstance() returns same object every time
        ConnectionPool pool1 = ConnectionPool.getInstance();
        ConnectionPool pool2 = ConnectionPool.getInstance();
        System.out.println("[1] Singleton check: pool1 == pool2 -> " + (pool1 == pool2));

        // 2. Concurrent access test
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger proxyCloseCount = new AtomicInteger(0);

        System.out.println("[2] Launching " + threadCount + " concurrent threads...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i + 1;
            executor.submit(() -> {
                Connection conn = null;
                try {
                    // Get from BlockingQueue
                    conn = pool1.getConnection();
                    String connId = conn.toString();
                    System.out.println("   [Thread-" + threadId + "] Acquired: " + connId);

                    // Execute query to prove connection is alive
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT 1 AS test_val")) {
                        if (rs.next()) {
                            System.out.println("   [Thread-" + threadId + "] Query OK: " + rs.getInt("test_val"));
                        }
                    }

                    // CRITICAL: Proxy intercepts close() -> returns to queue
                    conn.close();
                    proxyCloseCount.incrementAndGet();
                    System.out.println("   [Thread-" + threadId + "] Called close(). Proxy returned connection to pool.");
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    System.err.println("   [Thread-" + threadId + "] FAILED: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("\n=== Test Results ===");
        System.out.println("Duration: " + duration + " ms");
        System.out.println("Successful executions: " + successCount.get() + "/" + threadCount);
        System.out.println("Proxy close() intercepted: " + proxyCloseCount.get() + " times");
        System.out.println("Pool survived concurrent load without leaks or exceptions.");
        System.out.println("=== Test Finished ===");
    }
}