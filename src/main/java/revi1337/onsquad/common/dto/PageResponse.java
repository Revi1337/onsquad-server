package revi1337.onsquad.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        int size,
        int page,
        int totalPages,
        long totalCount,
        int resultsSize,
        List<T> results
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getSize(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumberOfElements(),
                page.getContent()
        );
    }
}
