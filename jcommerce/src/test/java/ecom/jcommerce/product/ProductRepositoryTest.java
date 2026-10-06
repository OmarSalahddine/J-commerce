package ecom.jcommerce.product;

import ecom.jcommerce.TestcontainersConfiguration;
import ecom.jcommerce.config.JpaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Persistence slice against a real PostgreSQL (Testcontainers). Flyway applies the
// V1 migration, Hibernate validates the mapping, and each test rolls back.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaConfig.class})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void save_assignsIdAuditFieldsAndVersion() {
        Product saved = repository.saveAndFlush(product("KB-001", "peripherals"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getVersion()).isZero();
    }

    @Test
    void existsBySku_reflectsSavedRows() {
        repository.saveAndFlush(product("KB-001", "peripherals"));

        assertThat(repository.existsBySku("KB-001")).isTrue();
        assertThat(repository.existsBySku("NOPE")).isFalse();
    }

    @Test
    void save_rejectsDuplicateSku_viaDbConstraint() {
        repository.saveAndFlush(product("KB-001", "peripherals"));

        assertThatThrownBy(() -> repository.saveAndFlush(product("KB-001", "office")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByCategoryIgnoreCase_filtersAndPages() {
        repository.save(product("A", "peripherals"));
        repository.save(product("B", "Peripherals"));
        repository.save(product("C", "office"));
        repository.flush();

        Page<Product> page = repository.findByCategoryIgnoreCase(
                "PERIPHERALS", PageRequest.of(0, 1, Sort.by("sku")));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Product::getSku).containsExactly("A");
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void update_incrementsVersion() {
        Product saved = repository.saveAndFlush(product("KB-001", "peripherals"));
        Integer before = saved.getVersion();

        saved.setStock(99);
        repository.saveAndFlush(saved);

        assertThat(saved.getVersion()).isEqualTo(before + 1);
    }

    private static Product product(String sku, String category) {
        return new Product(sku, "Name " + sku, null, new BigDecimal("10.00"), 1, category);
    }
}
