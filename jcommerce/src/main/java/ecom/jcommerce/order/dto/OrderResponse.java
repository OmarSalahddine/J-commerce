package ecom.jcommerce.order.dto;

import ecom.jcommerce.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderLineResponse> lines,
        Instant createdAt,
        Instant updatedAt
) {
}