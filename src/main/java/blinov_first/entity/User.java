package blinov_first.entity;

/**
 * Represents an application user stored in the {@code users} table.
 *
 * The {@code lastname} column is used as the unique login name.
 * Passwords are stored as SHA-256 hex digests (MySQL {@code SHA2(?, 256)}).
 *
 * {@code equals()} / {@code hashCode()} use only {@code id} (primary key),
 * consistent with the JPA recommendation for entity identity.
 * {@code toString()} deliberately omits the password for security.
 * {@code java.util.Objects} is not used (per course requirements).
 */
public class User extends AbstractEntity {

    private String  lastname;
    private String  password;
    private String  email;
    private String  phone;
    private boolean active;
    private String  role;
    private String  confirmationToken;

    public User() {}

    public User(long id, String lastname, String password,
                String email, String phone, boolean active, String role) {
        super(id);
        this.lastname  = lastname;
        this.password  = password;
        this.email     = email;
        this.phone     = phone;
        this.active    = active;
        this.role      = role;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public String  getLastname()                       { return lastname; }
    public void    setLastname(String lastname)        { this.lastname = lastname; }

    /** Alias so Spring Security can use the login name conveniently. */
    public String  getLogin()                          { return lastname; }
    public void    setLogin(String login)              { this.lastname = login; }

    public String  getPassword()                       { return password; }
    public void    setPassword(String password)        { this.password = password; }

    public String  getEmail()                          { return email; }
    public void    setEmail(String email)              { this.email = email; }

    public String  getPhone()                          { return phone; }
    public void    setPhone(String phone)              { this.phone = phone; }

    public boolean isActive()                          { return active; }
    public void    setActive(boolean active)           { this.active = active; }

    public String  getRole()                           { return role; }
    public void    setRole(String role)                { this.role = role; }

    public String  getConfirmationToken()                          { return confirmationToken; }
    public void    setConfirmationToken(String confirmationToken)  { this.confirmationToken = confirmationToken; }

    // ----------------------------------------------------------------
    // Object overrides — no java.util.Objects
    // ----------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User other = (User) o;
        if (getId() != other.getId()) return false;
        if (active != other.active) return false;
        if (lastname == null ? other.lastname != null : !lastname.equals(other.lastname)) return false;
        if (email    == null ? other.email    != null : !email.equals(other.email))       return false;
        return role == null ? other.role == null : role.equals(other.role);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (email    != null ? email.hashCode()    : 0);
        result = 31 * result + (role     != null ? role.hashCode()     : 0);
        result = 31 * result + (active   ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id="       + getId() +
                ", lastname='" + lastname + '\'' +
                ", email='"    + email    + '\'' +
                ", phone='"    + phone    + '\'' +
                ", active="    + active   +
                ", role='"     + role     + '\'' +
                '}';
    }
}
