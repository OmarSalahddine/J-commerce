package ecom.jcommerce.product;

import ecom.jcommerce.common.PageResponse;
import ecom.jcommerce.exception.DuplicateResourceException;
import ecom.jcommerce.exception.ResourceNotFoundException;
import ecom.jcommerce.product.dto.CreateProductRequest;
import ecom.jcommerce.product.dto.ProductResponse;
import ecom.jcommerce.product.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pure unit test: no Spring context. The repository is a mock, the mapper is real
// (it has no dependencies), so only ProductService's rules are under test.
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository, new ProductMapper());
    }

    @Test
    void create_savesAndReturnsResponse_whenSkuIsNew() {
        CreateProductRequest request = createRequest("KB-001");
        when(repository.existsBySku("KB-001")).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse response = service.create(request);

        assertThat(response.sku()).isEqualTo("KB-001");
        assertThat(response.name()).isEqualTo("Keyboard");
        assertThat(response.price()).isEqualByComparingTo("89.99");
        verify(repository).save(any(Product.class));
    }

    @Test
    void create_throwsDuplicate_whenSkuExists() {
        when(repository.existsBySku("KB-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(createRequest("KB-001")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("KB-001");

        verify(repository, never()).save(any());
    }

    @Test
    void getById_returnsResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(product("KB-001")));

        ProductResponse response = service.getById(1L);

        assertThat(response.sku()).isEqualTo("KB-001");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void list_usesFindAll_whenNoCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product("A"), product("B")), pageable, 2);
        when(repository.findAll(pageable)).thenReturn(page);

        PageResponse<ProductResponse> result = service.list(null, pageable);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        verify(repository, never()).findByCategoryIgnoreCase(any(), any());
    }

    @Test
    void list_filtersByCategory_whenProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByCategoryIgnoreCase("peripherals", pageable))
                .thenReturn(new PageImpl<>(List.of(product("A")), pageable, 1));

        PageResponse<ProductResponse> result = service.list("peripherals", pageable);

        assertThat(result.content()).hasSize(1);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void update_appliesChangesToManagedEntity() {
        Product existing = product("KB-001");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        ProductResponse response = service.update(1L,
                new UpdateProductRequest("Keyboard v2", "desc", new BigDecimal("79.99"), 5, "office"));

        assertThat(response.name()).isEqualTo("Keyboard v2");
        assertThat(response.stock()).isEqualTo(5);
        assertThat(existing.getName()).isEqualTo("Keyboard v2");
        assertThat(response.sku()).isEqualTo("KB-001");
        verify(repository).flush();
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    private static CreateProductRequest createRequest(String sku) {
        return new CreateProductRequest(sku, "Keyboard", "desc", new BigDecimal("89.99"), 10, "peripherals");
    }

    private static Product product(String sku) {
        return new Product(sku, "Keyboard", "desc", new BigDecimal("89.99"), 10, "peripherals");
    }
}
