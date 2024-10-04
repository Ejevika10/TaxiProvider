package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.PageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper
public interface PageMapper {
    @Mapping(target = "content", expression = "java(page.getContent())")
    @Mapping(target = "pageNumber", expression = "java(page.getPageable().getPageNumber())")
    @Mapping(target = "pageSize", expression = "java(page.getPageable().getPageSize())")
    @Mapping(target = "totalPages", expression = "java(page.getPageable().getTotalPages())")
    @Mapping(target = "totalElements", expression = "java(page.getTotalElements())")
    <T> PageDTO<T> pageToDto (Page<T> page);
}
