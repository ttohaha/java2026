package blinov_first.entity;

import java.util.Objects;

public class Item extends AbstractEntity {

    private String name;
    private double price;
    private String description;

    public Item() {
    }

    public Item(long id, String name, double price, String description) {
        super(id);
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.price, price) == 0 &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                getId() == item.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, price, description);
    }

    @Override
    public String toString() {
        return "Item{id=" + getId() + ", name='" + name + "', price=" + price + "}";
    }
}