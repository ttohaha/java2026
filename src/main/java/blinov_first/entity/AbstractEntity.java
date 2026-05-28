package blinov_first.entity;

/**
 * Base entity class.  All entities must override {@code toString()},
 * {@code equals()}, and {@code hashCode()} — {@code java.util.Objects}
 * helper methods are intentionally not used (per course requirements).
 */
public abstract class AbstractEntity {

    private long id;

    public AbstractEntity() {}

    public AbstractEntity(long id) {
        this.id = id;
    }

    public long getId()           { return id; }
    public void setId(long id)    { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
