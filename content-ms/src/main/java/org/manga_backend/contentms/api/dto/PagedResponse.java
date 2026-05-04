package org.manga_backend.contentms.api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedResponse(Page<T> pageData) {
        this.content = pageData.getContent();
        this.pageNumber = pageData.getNumber();
        this.pageSize = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
        this.last = pageData.isLast();
    }
}
