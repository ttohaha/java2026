/**
 * Pre-Spring connection pool concurrency diagnostic.
 * Connection pooling is now managed by HikariCP via Spring Boot auto-configuration.
 *
 * @deprecated Replaced by HikariCP.
 */
@Deprecated
public class PoolDiagnosticTest {
    // Removed — HikariCP thread-safety is tested by the HikariCP project itself.
}
