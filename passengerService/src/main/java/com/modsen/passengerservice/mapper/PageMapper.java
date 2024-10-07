package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PageDTO;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = PassengerMapper.class)
public interface PageMapper {
    default <T> PageDTO<T> pageToDto(Page<T> page) {
        PageDTO<T> dto = new PageDTO<T>();
        dto.setContent(page.getContent());
        dto.setPageNumber(page.getPageable().getPageNumber());
        dto.setPageSize(page.getPageable().getPageSize());
        dto.setTotalPages(page.getTotalPages());
        dto.setTotalElements(page.getTotalElements());
        return dto;
    }
}

