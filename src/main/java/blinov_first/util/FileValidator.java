package blinov_first.util;

import blinov_first.config.UploadConfig;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class FileValidator {

    private FileValidator() {}

    private static final Set<String> ALLOWED_EXTS = Arrays.stream(UploadConfig.ALLOWED_EXTENSIONS.split(","))
            .map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());
    private static final Set<String> ALLOWED_TYPES = Arrays.stream(UploadConfig.ALLOWED_CONTENT_TYPES.split(","))
            .map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());

    public static boolean isAllowedType(String filename, String contentType) {
        if (filename == null || contentType == null) return false;
        String ext = getExtension(filename).toLowerCase();
        return ALLOWED_EXTS.contains(ext) && ALLOWED_TYPES.contains(contentType.toLowerCase());
    }

    public static boolean isWithinSizeLimit(long fileSize) {
        return fileSize >= 0 && fileSize <= UploadConfig.MAX_FILE_SIZE_BYTES;
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) return "unnamed_file";
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? "" : filename.substring(dot + 1);
    }
}