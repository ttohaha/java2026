package blinov_first.util;

import java.util.UUID;

/**
 * Generates unique filenames for stored uploads.
 * The original file extension is preserved.
 */
public final class FileNameGenerator {

    private FileNameGenerator() {}

    public static String generate(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }
}
