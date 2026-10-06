package ecom.jcommerce.product;

import ecom.jcommerce.product.dto.CreateProductRequest;
import ecom.jcommerce.product.dto.ProductResponse;
import ecom.jcommerce.product.dto.UpdateProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return new Product(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category()
        );
    }

    public void applyUpdate(Product product, UpdateProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
