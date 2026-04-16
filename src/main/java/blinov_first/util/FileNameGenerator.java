package blinov_first.util;

import java.util.UUID;

public final class FileNameGenerator {

    private FileNameGenerator() {}

    public static String generateSafeName(String originalName) {
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) ext = originalName.substring(dot);
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ext;
    }
}