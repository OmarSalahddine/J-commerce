package ecom.jcommerce.order;

import ecom.jcommerce.common.BaseEntity;
import ecom.jcommerce.exception.BusinessRuleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // cascade = ALL: saving/deleting the order saves/deletes its lines.
    // orphanRemoval: removing a line from this list deletes the row.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {
    }

    public Order(String customerEmail) {
        this.customerEmail = customerEmail;
        this.status = OrderStatus.PENDING;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
        line.setOrder(this);
        totalAmount = totalAmount.add(line.lineTotal());
    }

    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new BusinessRuleException("Order " + getId() + " cannot be cancelled from status " + status);
        }
        status = OrderStatus.CANCELLED;
    }

    public String getCustomerEmail() { return customerEmail; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<OrderLine> getLines() { return Collections.unmodifiableList(lines); }
}