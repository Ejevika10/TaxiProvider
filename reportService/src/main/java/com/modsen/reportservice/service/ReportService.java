package com.modsen.reportservice.service;

public interface ReportService {

    byte[] createReport(String driverId, String bearerToken);
}
