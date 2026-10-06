package ecom.jcommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Email String customerEmail,
        @NotEmpty @Valid List<OrderLineRequest> lines
) {
}