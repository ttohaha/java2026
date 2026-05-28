package blinov_first.entity;

import java.time.LocalDateTime;

/**
 * Represents a single contact in a user's phone book.
 *
 * {@code equals()} / {@code hashCode()} are based on the primary key
 * and the three data fields that uniquely describe a contact.
 * {@code java.util.Objects} is not used (per course requirements).
 */
public class PhoneEntry extends AbstractEntity {

    private Long          userId;
    private String        contactName;
    private String        contactPhone;
    private String        contactEmail;
    private LocalDateTime createdAt;

    public PhoneEntry() {}

    public PhoneEntry(long id, Long userId,
                      String contactName, String contactPhone, String contactEmail) {
        super(id);
        this.userId       = userId;
        this.contactName  = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public Long   getUserId()                          { return userId; }
    public void   setUserId(Long userId)               { this.userId = userId; }

    public String getContactName()                     { return contactName; }
    public void   setContactName(String contactName)   { this.contactName = contactName; }

    public String getContactPhone()                    { return contactPhone; }
    public void   setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail()                    { return contactEmail; }
    public void   setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime v)   { this.createdAt = v; }

    // ----------------------------------------------------------------
    // Object overrides — no java.util.Objects
    // ----------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneEntry other = (PhoneEntry) o;
        if (getId() != other.getId()) return false;
        if (userId == null ? other.userId != null : !userId.equals(other.userId)) return false;
        if (contactName  == null ? other.contactName  != null
                                 : !contactName.equals(other.contactName))   return false;
        return contactPhone == null ? other.contactPhone == null
                                    : contactPhone.equals(other.contactPhone);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + (userId       != null ? userId.hashCode()       : 0);
        result = 31 * result + (contactName  != null ? contactName.hashCode()  : 0);
        result = 31 * result + (contactPhone != null ? contactPhone.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhoneEntry{" +
                "id="           + getId()       +
                ", userId="     + userId        +
                ", name='"      + contactName   + '\'' +
                ", phone='"     + contactPhone  + '\'' +
                ", email='"     + contactEmail  + '\'' +
                '}';
    }
}
