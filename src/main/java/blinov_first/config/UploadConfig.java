package blinov_first.config;

import java.io.File;

public final class UploadConfig {

    private UploadConfig() {}

    public static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "blinov_uploads";
    public static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10 MB
    public static final String ALLOWED_EXTENSIONS = "jpg,jpeg,png,gif,pdf,txt,doc,docx";
    public static final String ALLOWED_CONTENT_TYPES = "image/jpeg,image/png,image/gif,application/pdf,text/plain,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static void ensureUploadDirectoryExists() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory: " + UPLOAD_DIR);
        }
    }
}