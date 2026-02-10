package com.arquitecsoft.gestion.infrastructure.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageResponse<T> {

    private List<T> data;
    private PageInfo pagination;

    public PageResponse() {
    }

    public PageResponse(List<T> data, PageInfo pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public static <E, D> PageResponse<D> from(Page<E> page, Function<E, D> mapper) {
        List<D> data = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        PageInfo pagination = new PageInfo(
                page.getNumber() + 1,  // 1-based page number
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new PageResponse<>(data, pagination);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageInfo getPagination() {
        return pagination;
    }

    public void setPagination(PageInfo pagination) {
        this.pagination = pagination;
    }

    public static class PageInfo {
        private int page;
        private int pageSize;
        private long totalItems;
        private int totalPages;

        public PageInfo() {
        }

        public PageInfo(int page, int pageSize, long totalItems, int totalPages) {
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}
