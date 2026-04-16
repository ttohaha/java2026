package blinov_first.factory;

import blinov_first.service.PhoneEntryService;
import blinov_first.service.impl.PhoneEntryServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PhoneEntryServiceFactory {

    private static final Logger LOGGER = LogManager.getLogger(PhoneEntryServiceFactory.class);
    private static volatile PhoneEntryService instance;

    private PhoneEntryServiceFactory() {}

    public static PhoneEntryService getPhoneEntryService() {
        if (instance == null) {
            synchronized (PhoneEntryServiceFactory.class) {
                if (instance == null) {
                    instance = PhoneEntryServiceImpl.getInstance();
                    LOGGER.info("PhoneEntryService instance created via factory");
                }
            }
        }
        return instance;
    }

    public static void resetForTesting() {
        instance = null;
    }
}