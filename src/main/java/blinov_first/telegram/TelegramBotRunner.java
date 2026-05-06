package blinov_first.telegram;

import blinov_first.dao.impl.UserDaoImpl;
import blinov_first.exception.DaoException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TelegramBotRunner implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(TelegramBotRunner.class);

    private static final String CMD_START         = "/start";
    private static final String CMD_CONFIRM       = "/confirm";
    private static final int    RETRY_DELAY_MS    = 5_000;

    private static final String MSG_WELCOME =
            "Welcome! Send your confirmation token like this:\n/start YOUR_TOKEN";

    private static final String MSG_SUCCESS =
            "Your account has been successfully confirmed!\n"
                    + "You can now log in at the application.";

    private static final String MSG_ALREADY =
            "This account is already confirmed or the token has expired.\n"
                    + "Try logging in or register again.";

    private static final String MSG_UNKNOWN =
            "Unknown command. Send your confirmation token like:\n/start YOUR_TOKEN";

    private final TelegramApiClient apiClient;
    private final int               pollingTimeout;
    private volatile boolean        running = true;
    private long                    offset  = 0L;

    public TelegramBotRunner() {
        TelegramBotConfig config = TelegramBotConfig.getInstance();
        this.apiClient      = new TelegramApiClient(config.getBotToken());
        this.pollingTimeout = config.getPollingTimeout();
    }

    @Override
    public void run() {
        LOGGER.info("Telegram bot polling started");

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                List<TelegramUpdate> updates = apiClient.getUpdates(offset, pollingTimeout);
                for (TelegramUpdate update : updates) {
                    processUpdate(update);
                    offset = update.getUpdateId() + 1L;
                }
            } catch (Exception e) {
                if (running) {
                    LOGGER.warn("Polling cycle error, retrying in {}ms: {}", RETRY_DELAY_MS, e.getMessage());
                    sleepSafely(RETRY_DELAY_MS);
                }
            }
        }

        LOGGER.info("Telegram bot polling stopped");
    }

    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

    private void processUpdate(TelegramUpdate update) {
        long   chatId = update.getChatId();
        String text   = update.getText();

        if (chatId <= 0 || text == null || text.isBlank()) {
            return;
        }

        LOGGER.debug("Telegram message from chatId={}: '{}'", chatId, text);
        String trimmed = text.trim();

        if (trimmed.startsWith(CMD_START)) {
            handleConfirmCommand(chatId, trimmed, CMD_START);
        } else if (trimmed.startsWith(CMD_CONFIRM)) {
            handleConfirmCommand(chatId, trimmed, CMD_CONFIRM);
        } else {
            apiClient.sendMessage(chatId, MSG_UNKNOWN);
        }
    }

    private void handleConfirmCommand(long chatId, String text, String command) {
        String token = extractToken(text, command);

        if (token.isEmpty()) {
            apiClient.sendMessage(chatId, MSG_WELCOME);
            return;
        }

        try {
            java.util.Optional<Long> userIdOpt =
                    UserDaoImpl.getInstance().findUserIdByToken(token);

            if (userIdOpt.isEmpty()) {
                LOGGER.warn("Token not found or already used. chatId={}", chatId);
                apiClient.sendMessage(chatId, MSG_ALREADY);
                return;
            }

            long userId = userIdOpt.get();
            boolean confirmed = UserServiceImpl.getInstance().confirmRegistration(token);

            if (confirmed) {
                LOGGER.info("Account confirmed via Telegram. chatId={} userId={}", chatId, userId);
                saveTelegramChatId(userId, chatId);
                apiClient.sendMessage(chatId, MSG_SUCCESS);
            } else {
                LOGGER.warn("Token activation failed. chatId={}", chatId);
                apiClient.sendMessage(chatId, MSG_ALREADY);
            }
        } catch (ServiceException | DaoException e) {
            LOGGER.error("Confirmation error via Telegram for chatId={}", chatId, e);
            apiClient.sendMessage(chatId, MSG_ALREADY);
        }
    }

    private void saveTelegramChatId(long userId, long chatId) {
        try {
            UserDaoImpl.getInstance().saveTelegramChatId(userId, chatId);
        } catch (DaoException e) {
            LOGGER.warn("Could not save telegram_chat_id for chatId={}", chatId, e);
        }
    }

    private String extractToken(String text, String command) {
        String after = text.substring(command.length()).trim();
        if (after.isEmpty()) {
            return "";
        }
        String[] parts = after.split("\\s+", 2);
        return parts[0].trim();
    }

    private void sleepSafely(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}