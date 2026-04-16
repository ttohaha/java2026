package blinov_first.factory;

import blinov_first.service.MediaFileService;
import blinov_first.service.impl.MediaFileServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MediaFileServiceFactory {

    private static final Logger LOGGER = LogManager.getLogger(MediaFileServiceFactory.class);
    private static volatile MediaFileService instance;

    private MediaFileServiceFactory() {}

    public static MediaFileService getMediaFileService() {
        if (instance == null) {
            synchronized (MediaFileServiceFactory.class) {
                if (instance == null) {
                    instance = MediaFileServiceImpl.getInstance();
                    LOGGER.info("MediaFileService instance created via factory");
                }
            }
        }
        return instance;
    }
}