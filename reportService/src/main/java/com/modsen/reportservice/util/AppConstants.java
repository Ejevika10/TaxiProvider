package com.modsen.reportservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String REPORT_TEMPLATE_NAME = "classpath:reports/driver-report.jrxml";

    public static final String SUBREPORT_TEMPLATE_NAME = "classpath:reports/ride-list-report.jrxml";

    public static final String REPORT_FILE_NAME = "driver-report.pdf";
    public static final String REPORT_SUBJECT = "Taxi provider app";
    public static final String REPORT_MESSAGE = "It's a month report about driver activity";

}