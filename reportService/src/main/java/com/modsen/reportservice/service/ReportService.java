package com.modsen.reportservice.service;

import com.modsen.reportservice.dto.DriverResponseDto;

public interface ReportService {

    byte[] createReport(DriverResponseDto driverResponseDto, String bearerToken);
}
