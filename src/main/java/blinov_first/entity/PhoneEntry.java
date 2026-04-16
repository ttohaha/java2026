package blinov_first.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class PhoneEntry extends AbstractEntity {

    @NotBlank(message = "Contact name cannot be empty")
    @Size(max = 100, message = "Contact name exceeds maximum length")
    private String contactName;

    @NotBlank(message = "Contact phone cannot be empty")
    @Size(max = 20, message = "Contact phone exceeds maximum length")
    private String contactPhone;

    @Size(max = 100, message = "Contact email exceeds maximum length")
    private String contactEmail;

    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PhoneEntry() {}

    public PhoneEntry(String contactName, String contactPhone, String contactEmail, Long userId) {
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.userId = userId;
    }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}