package blinov_first.telegram;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class TelegramBotListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger(TelegramBotListener.class);

    private static final String THREAD_NAME = "telegram-bot-polling";

    private Thread botThread;
    private TelegramBotRunner botRunner;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        TelegramBotConfig config = TelegramBotConfig.getInstance();

        if (!config.isConfigured()) {
            LOGGER.warn("Telegram bot is NOT configured — skipping startup. "
                    + "Fill in telegram.properties to enable.");
            return;
        }

        botRunner = new TelegramBotRunner();
        botThread = new Thread(botRunner, THREAD_NAME);
        botThread.setDaemon(true);
        botThread.start();

        LOGGER.info("Telegram bot started: @{}", config.getBotUsername());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (botRunner != null) {
            botRunner.stop();
        }
        if (botThread != null) {
            botThread.interrupt();
            LOGGER.info("Telegram bot stopped");
        }
    }
}
