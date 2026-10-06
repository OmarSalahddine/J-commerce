package ecom.jcommerce.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int stock,
        String category,
        Instant createdAt,
        Instant updatedAt
) {
}
