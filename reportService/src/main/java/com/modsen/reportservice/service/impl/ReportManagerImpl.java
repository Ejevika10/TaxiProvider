package com.modsen.reportservice.service.impl;

import com.modsen.reportservice.client.driver.DriverClientService;
import com.modsen.reportservice.dto.DriverResponseDto;
import com.modsen.reportservice.dto.PageDto;
import com.modsen.reportservice.service.ReportManager;
import com.modsen.reportservice.service.ReportService;
import com.modsen.reportservice.service.SenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.modsen.reportservice.util.AppConstants.REPORT_FILE_NAME;
import static com.modsen.reportservice.util.AppConstants.REPORT_MESSAGE;
import static com.modsen.reportservice.util.AppConstants.REPORT_SUBJECT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportManagerImpl implements ReportManager {

    private final DriverClientService driverClientService;
    private final ReportService reportService;
    private final SenderService senderService;
    private final KeycloakService keycloakService;

    @Scheduled(fixedDelayString = "${interval}")
    public void sendReportForAllDrivers() {
        String bearerToken = getCurrentToken();
        int numOfPages = 0;
        PageDto<DriverResponseDto> driversPage;
        do {
            driversPage = driverClientService.getPageDrivers(numOfPages, 5, bearerToken);
            numOfPages++;

            List<DriverResponseDto> drivers = driversPage.content();
            for (DriverResponseDto driver : drivers) {
                sendReportForDriver(driver, driver.email(), bearerToken);
            }
        } while (numOfPages < driversPage.totalPages());
    }

    @Override
    public ByteArrayResource sendReportForDriverById(String driverId, String email, String bearerToken) {
        DriverResponseDto driver = driverClientService.getDriverById(driverId, bearerToken);
        return sendReportForDriver(driver, email, bearerToken);
    }

    private ByteArrayResource sendReportForDriver(DriverResponseDto driverResponseDto, String email, String bearerToken) {
        byte[] pdfReport = reportService.createReport(driverResponseDto, bearerToken);

        ByteArrayResource resource = new ByteArrayResource(pdfReport);

        senderService.sendReport(email,
                REPORT_SUBJECT,
                REPORT_MESSAGE,
                REPORT_FILE_NAME,
                resource);
        return resource;
    }

    private String getCurrentToken() {
        String token = keycloakService.getAdminToken();
        return "bearer " + token;
    }
}
