package blinov_first.entity;

import java.time.LocalDateTime;

/**
 * Represents a file uploaded by a user (metadata only — file bytes are on disk).
 *
 * {@code equals()} / {@code hashCode()} use the primary key and stored
 * filename, which is unique per file on the filesystem.
 * {@code java.util.Objects} is not used (per course requirements).
 */
public class MediaFile extends AbstractEntity {

    private Long          userId;
    private String        storedFilename;
    private String        originalFilename;
    private String        contentType;
    private long          fileSize;
    private String        filePath;
    private LocalDateTime uploadDate;

    public MediaFile() {}

    public MediaFile(long id, Long userId,
                     String storedFilename, String originalFilename,
                     String contentType, long fileSize, String filePath) {
        super(id);
        this.userId           = userId;
        this.storedFilename   = storedFilename;
        this.originalFilename = originalFilename;
        this.contentType      = contentType;
        this.fileSize         = fileSize;
        this.filePath         = filePath;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public Long          getUserId()                            { return userId; }
    public void          setUserId(Long userId)                 { this.userId = userId; }

    public String        getStoredFilename()                    { return storedFilename; }
    public void          setStoredFilename(String v)            { this.storedFilename = v; }

    public String        getOriginalFilename()                  { return originalFilename; }
    public void          setOriginalFilename(String v)          { this.originalFilename = v; }

    public String        getContentType()                       { return contentType; }
    public void          setContentType(String contentType)     { this.contentType = contentType; }

    public long          getFileSize()                          { return fileSize; }
    public void          setFileSize(long fileSize)             { this.fileSize = fileSize; }

    public String        getFilePath()                          { return filePath; }
    public void          setFilePath(String filePath)           { this.filePath = filePath; }

    public LocalDateTime getUploadDate()                        { return uploadDate; }
    public void          setUploadDate(LocalDateTime uploadDate){ this.uploadDate = uploadDate; }

    // ----------------------------------------------------------------
    // Object overrides — no java.util.Objects
    // ----------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile other = (MediaFile) o;
        if (getId() != other.getId()) return false;
        if (fileSize != other.fileSize) return false;
        if (storedFilename == null ? other.storedFilename != null
                                   : !storedFilename.equals(other.storedFilename)) return false;
        return userId == null ? other.userId == null : userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + (userId         != null ? userId.hashCode()         : 0);
        result = 31 * result + (storedFilename  != null ? storedFilename.hashCode() : 0);
        result = 31 * result + Long.hashCode(fileSize);
        return result;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "id="                + getId()           +
                ", userId="          + userId            +
                ", originalFilename='" + originalFilename + '\'' +
                ", contentType='"    + contentType       + '\'' +
                ", fileSize="        + fileSize          +
                '}';
    }
}
