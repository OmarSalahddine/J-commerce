package ecom.jcommerce.product;

import ecom.jcommerce.common.BaseEntity;
import ecom.jcommerce.exception.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(length = 100)
    private String category;

    // JPA needs a no-arg constructor to instantiate rows it loads from the DB.
    protected Product() {
    }

    public Product(String sku, String name, String description, BigDecimal price, int stock, String category) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public void decreaseStock(int quantity) {
        if (quantity > stock) {
            throw new BusinessRuleException(
                    "Insufficient stock for " + sku + ": requested " + quantity + ", available " + stock);
        }
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }
}
