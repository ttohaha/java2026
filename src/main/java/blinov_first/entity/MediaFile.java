package blinov_first.entity;

import java.time.LocalDateTime;

public class MediaFile extends AbstractEntity {

    private Long userId;
    private String storedFilename;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;

    public MediaFile() {}

    public MediaFile(Long userId, String storedFilename, String originalFilename,
                     String contentType, Long fileSize, String filePath) {
        this.userId = userId;
        this.storedFilename = storedFilename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}