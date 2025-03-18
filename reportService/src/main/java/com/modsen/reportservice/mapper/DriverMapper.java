package com.modsen.reportservice.mapper;

import com.modsen.reportservice.dto.DriverResponseDto;
import com.modsen.reportservice.model.DriverForReport;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    DriverForReport toDriverForReport(DriverResponseDto driver);
}
