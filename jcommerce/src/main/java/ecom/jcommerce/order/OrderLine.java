package ecom.jcommerce.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Reference by id, not @ManyToOne Product: an order must stay valid
    // even if the product changes or disappears. The columns below are the snapshot.
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_sku", nullable = false, length = 64)
    private String productSku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    protected OrderLine() {
    }

    public OrderLine(Long productId, String productSku, String productName, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductSku() { return productSku; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}