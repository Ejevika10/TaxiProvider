package com.modsen.reportservice.service;

import org.springframework.core.io.ByteArrayResource;

public interface ReportManager {

    ByteArrayResource sendReportForDriverById(String driverId, String email, String bearerToken);
}
