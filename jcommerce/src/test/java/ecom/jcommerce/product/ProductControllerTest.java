package ecom.jcommerce.product;

import ecom.jcommerce.common.PageResponse;
import ecom.jcommerce.config.SecurityConfig;
import ecom.jcommerce.exception.DuplicateResourceException;
import ecom.jcommerce.exception.ResourceNotFoundException;
import ecom.jcommerce.product.dto.CreateProductRequest;
import ecom.jcommerce.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web slice only: controller + @RestControllerAdvice + validation + security filters.
// ProductService is mocked, so no database and no business logic runs here.
@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    private static final String VALID_BODY = """
            {"sku":"KB-001","name":"Keyboard","description":"d","price":89.99,"stock":10,"category":"peripherals"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Test
    @WithMockUser
    void create_returns201WithLocation() throws Exception {
        when(service.create(any(CreateProductRequest.class))).thenReturn(response(1L));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("KB-001"));
    }

    @Test
    void create_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized());

        verify(service, never()).create(any());
    }

    @Test
    @WithMockUser
    void create_returns400WithFieldErrors_whenBodyInvalid() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"","name":"","price":-1,"stock":-5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.errors.sku").value("must not be blank"))
                .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stock").exists());

        verify(service, never()).create(any());
    }

    @Test
    @WithMockUser
    void create_returns409_whenSkuDuplicate() throws Exception {
        when(service.create(any(CreateProductRequest.class)))
                .thenThrow(new DuplicateResourceException("Product with sku KB-001 already exists"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Product with sku KB-001 already exists"));
    }

    @Test
    void getById_isPublic_andReturns200() throws Exception {
        when(service.getById(1L)).thenReturn(response(1L));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(89.99));
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        when(service.getById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Product with id 99 not found"));
    }

    @Test
    void list_isPublic_andPassesCategoryAndPaging() throws Exception {
        when(service.list(eq("peripherals"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(response(1L)), 0, 5, 1, 1, true));

        mockMvc.perform(get("/api/products").param("category", "peripherals").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    @WithMockUser
    void delete_returns404_whenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Product", 99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isUnauthorized());

        verify(service, never()).delete(any());
    }

    private static ProductResponse response(Long id) {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new ProductResponse(id, "KB-001", "Keyboard", "d", new BigDecimal("89.99"), 10, "peripherals", now, now);
    }
}
