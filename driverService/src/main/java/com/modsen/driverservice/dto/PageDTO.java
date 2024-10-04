package com.modsen.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int totalElements;
    private List<T> content;
}
