package com.modsen.reportservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String REPORT_NAME = "classpath:reports/driver-report.jrxml";
    public static final String SUBREPORT_NAME = "classpath:reports/ride-list-report.jrxml";

}