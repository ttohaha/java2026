package blinov_first.telegram;

/**
 * Singleton that held the Telegram bot token in the pre-Spring version.
 *
 * Replaced by {@link blinov_first.config.TelegramProperties}, which reads
 * the token from {@code application.properties} via {@code @ConfigurationProperties}.
 *
 * @deprecated Replaced by {@code TelegramProperties}.
 */
@Deprecated
public class TelegramBotConfig {
    private TelegramBotConfig() {}
}
