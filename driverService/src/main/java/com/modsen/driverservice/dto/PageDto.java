package com.modsen.driverservice.dto;

import java.util.List;

public record PageDto<T> (
    int pageNumber,
    int pageSize,
    int totalPages,
    long totalElements,
    List<T> content) {

    public PageDto(int pageNumber, int pageSize, int totalPages, long totalElements, List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;

    }
}
