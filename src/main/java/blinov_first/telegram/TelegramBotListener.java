package blinov_first.telegram;

/**
 * This class was a {@code @WebListener} that started the Telegram polling
 * loop on application startup in the pre-Spring servlet version.
 *
 * It has been replaced by {@link TelegramBotService}, which is a Spring
 * {@code @Component} implementing {@code CommandLineRunner} — it starts
 * the polling loop automatically after the Spring context is fully
 * initialized, with all dependencies injected by the IoC container.
 *
 * Retained as an empty placeholder for VCS traceability.
 *
 * @deprecated Replaced by {@link TelegramBotService}.
 */
@Deprecated
public class TelegramBotListener {
    // Intentionally empty — see JavaDoc above.
}
