package com.modsen.passengerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PageDTO<T> {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> content;
}
