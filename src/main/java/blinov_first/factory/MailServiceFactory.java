package blinov_first.factory;

/**
 * Factory that created a {@code MailServiceImpl} in the pre-Spring version.
 *
 * Replaced by Spring Boot auto-configuration of {@code JavaMailSender}
 * (via {@code spring.mail.*} properties) and {@code MailServiceImpl}
 * annotated with {@code @Service}.
 *
 * @deprecated Replaced by Spring DI + {@code MailServiceImpl}.
 */
@Deprecated
public final class MailServiceFactory {
    private MailServiceFactory() {}
}
