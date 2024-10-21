package com.modsen.rideservice.dto;

import java.util.List;

public record PageDto<T> (
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements,
        List<T> content) {
}