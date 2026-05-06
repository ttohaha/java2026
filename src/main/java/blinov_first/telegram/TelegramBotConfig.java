package blinov_first.telegram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TelegramBotConfig {

    private static final Logger LOGGER = LogManager.getLogger(TelegramBotConfig.class);

    private static final String PROPERTIES_FILE   = "telegram.properties";
    private static final String KEY_TOKEN         = "telegram.bot.token";
    private static final String KEY_USERNAME      = "telegram.bot.username";
    private static final String KEY_POLL_TIMEOUT  = "telegram.polling.timeout";

    private static final int DEFAULT_POLL_TIMEOUT = 25;

    private static final TelegramBotConfig INSTANCE = new TelegramBotConfig();

    private final String botToken;
    private final String botUsername;
    private final int    pollingTimeout;
    private final boolean configured;

    private TelegramBotConfig() {
        Properties props = new Properties();
        boolean loaded = false;
        try (InputStream is = TelegramBotConfig.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (is != null) {
                props.load(is);
                loaded = true;
            } else {
                LOGGER.warn("telegram.properties not found on classpath");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load telegram.properties", e);
        }

        this.botToken      = props.getProperty(KEY_TOKEN, "");
        this.botUsername   = props.getProperty(KEY_USERNAME, "");
        this.pollingTimeout = parseIntSafe(props.getProperty(KEY_POLL_TIMEOUT), DEFAULT_POLL_TIMEOUT);
        this.configured    = loaded
                && !botToken.isBlank()
                && !botToken.equals("PASTE_YOUR_BOT_TOKEN_HERE");
    }

    public static TelegramBotConfig getInstance() {
        return INSTANCE;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public int getPollingTimeout() {
        return pollingTimeout;
    }

    public boolean isConfigured() {
        return configured;
    }

    private int parseIntSafe(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
