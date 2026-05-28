package blinov_first.pool;

/**
 * Custom connection pool from the pre-Spring version.
 *
 * Replaced by Spring Boot's HikariCP auto-configuration.
 * All datasource settings are now in {@code application.properties}
 * under {@code spring.datasource.*}.
 *
 * @deprecated Replaced by HikariCP + Spring Boot auto-configuration.
 */
@Deprecated
public final class ConnectionPool {
    private ConnectionPool() {}

    /** @deprecated Use Spring's {@code JdbcTemplate} / {@code DataSource} instead. */
    @Deprecated
    public static ConnectionPool getInstance() {
        throw new UnsupportedOperationException(
                "ConnectionPool is no longer available. Use Spring DataSource.");
    }
}
