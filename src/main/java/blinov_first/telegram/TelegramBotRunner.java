package blinov_first.telegram;

/**
 * This class contained the Telegram long-polling loop in the pre-Spring
 * version and depended on static {@code getInstance()} accessors.
 *
 * The polling logic has been moved to {@link TelegramBotService}, which
 * receives all its dependencies via Spring constructor injection.
 *
 * Retained as an empty placeholder for VCS traceability.
 *
 * @deprecated Replaced by {@link TelegramBotService}.
 */
@Deprecated
public class TelegramBotRunner {
    // Intentionally empty — see JavaDoc above.
}
