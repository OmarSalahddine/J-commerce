package ecom.jcommerce.product;

import ecom.jcommerce.common.PageResponse;
import ecom.jcommerce.exception.DuplicateResourceException;
import ecom.jcommerce.exception.ResourceNotFoundException;
import ecom.jcommerce.product.dto.CreateProductRequest;
import ecom.jcommerce.product.dto.ProductResponse;
import ecom.jcommerce.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with sku " + request.sku() + " already exists");
        }
        Product saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    public ProductResponse getById(Long id) {
        return mapper.toResponse(find(id));
    }

    public PageResponse<ProductResponse> list(String category, Pageable pageable) {
        Page<Product> page = (category == null || category.isBlank())
                ? repository.findAll(pageable)
                : repository.findByCategoryIgnoreCase(category, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = find(id);
        mapper.applyUpdate(product, request);
        // The entity is managed, so Hibernate would flush the diff on commit anyway.
        // Flushing now runs the UPDATE (and the @LastModifiedDate / @Version
        // listeners) before we build the response, so the DTO reflects the new row.
        repository.flush();
        return mapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Product find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
