package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PageDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PassengerMapper.class)
public interface PageMapper {
    default <T> PageDto<T> pageToDto(Page<T> page) {
        return new PageDto<T>(page.getPageable().getPageNumber(), page.getPageable().getPageSize(), page.getTotalPages(), page.getTotalElements(), page.getContent());
    }
}

