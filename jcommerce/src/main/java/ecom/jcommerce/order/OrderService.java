package ecom.jcommerce.order;

import ecom.jcommerce.exception.ResourceNotFoundException;
import ecom.jcommerce.order.dto.CreateOrderRequest;
import ecom.jcommerce.order.dto.OrderLineRequest;
import ecom.jcommerce.order.dto.OrderResponse;
import ecom.jcommerce.product.Product;
import ecom.jcommerce.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional
    public OrderResponse place(CreateOrderRequest request) {
        Order order = new Order(request.customerEmail());

        for (OrderLineRequest lineRequest : request.lines()) {
            Product product = productRepository.findById(lineRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", lineRequest.productId()));

            product.decreaseStock(lineRequest.quantity());

            order.addLine(new OrderLine(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    product.getPrice(),
                    lineRequest.quantity()));
        }

        Order saved = orderRepository.save(order);
        orderRepository.flush();
        return mapper.toResponse(saved);
    }

    public OrderResponse getById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = find(id);
        order.cancel();

        for (OrderLine line : order.getLines()) {
            productRepository.findById(line.getProductId())
                    .ifPresent(product -> product.increaseStock(line.getQuantity()));
        }

        orderRepository.flush();
        return mapper.toResponse(order);
    }

    private Order find(Long id) {
        return orderRepository.findWithLinesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}