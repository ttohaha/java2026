package blinov_first.factory;

import blinov_first.service.MailService;
import blinov_first.service.impl.MailServiceImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MailServiceFactory {

    private static final Logger LOGGER = LogManager.getLogger(MailServiceFactory.class);
    private static final String MAIL_CONFIG_PATH = "mail.properties";

    private static volatile MailService instance;
    private static final Properties mailConfig = new Properties();
    private static boolean configLoaded = false;

    private MailServiceFactory() {}

    public static MailService getMailService() {
        if (instance == null) {
            synchronized (MailServiceFactory.class) {
                if (instance == null) {
                    loadConfigIfNeeded();
                    instance = new MailServiceImpl(mailConfig);
                    LOGGER.info("MailService instance created via factory");
                }
            }
        }
        return instance;
    }

    private static void loadConfigIfNeeded() {
        if (!configLoaded) {
            try (InputStream input = MailServiceFactory.class.getClassLoader().getResourceAsStream(MAIL_CONFIG_PATH)) {
                if (input != null) {
                    mailConfig.load(input);
                    configLoaded = true;
                    LOGGER.info("Mail configuration loaded from {}", MAIL_CONFIG_PATH);
                } else {
                    LOGGER.error("Configuration file {} not found in classpath", MAIL_CONFIG_PATH);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load mail configuration", e);
            }
        }
    }

    public static void resetInstanceForTesting() {
        instance = null;
    }
}