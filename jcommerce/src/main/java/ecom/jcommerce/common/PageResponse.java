package ecom.jcommerce.common;

import org.springframework.data.domain.Page;

import java.util.List;

// Spring Data's Page serializes with internal fields (pageable, sort, ...) that
// are not a stable API contract. This is the shape clients can rely on.
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
