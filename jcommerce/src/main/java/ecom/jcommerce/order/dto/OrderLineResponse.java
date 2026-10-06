package ecom.jcommerce.order.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long productId,
        String productSku,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}