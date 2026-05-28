package blinov_first.entity;

public class Item extends AbstractEntity {

    private String name;
    private double price;
    private String description;

    public Item() {}

    public Item(long id, String name, double price, String description) {
        super(id);
        this.name        = name;
        this.price       = price;
        this.description = description;
    }

    public String getName()        { return name; }
    public double getPrice()       { return price; }
    public String getDescription() { return description; }

    public void setName(String name)              { this.name = name; }
    public void setPrice(double price)            { this.price = price; }
    public void setDescription(String description){ this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (getId() != item.getId()) return false;
        if (Double.compare(item.price, price) != 0) return false;
        if (name == null ? item.name != null : !name.equals(item.name)) return false;
        return description == null ? item.description == null
                : description.equals(item.description);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + Double.hashCode(price);
        result = 31 * result + (name        != null ? name.hashCode()        : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item{id=" + getId() + ", name='" + name + "', price=" + price + "}";
    }
}
