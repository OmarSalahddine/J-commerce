package ecom.jcommerce.order;

import ecom.jcommerce.order.dto.OrderLineResponse;
import ecom.jcommerce.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getLines().stream().map(this::toResponse).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderLineResponse toResponse(OrderLine line) {
        return new OrderLineResponse(
                line.getProductId(),
                line.getProductSku(),
                line.getProductName(),
                line.getUnitPrice(),
                line.getQuantity(),
                line.lineTotal()
        );
    }
}