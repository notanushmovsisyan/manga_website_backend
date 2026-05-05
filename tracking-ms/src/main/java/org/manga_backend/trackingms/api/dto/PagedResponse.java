package org.manga_backend.trackingms.api.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PagedResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        boolean last
) {
    public PagedResponse(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}
