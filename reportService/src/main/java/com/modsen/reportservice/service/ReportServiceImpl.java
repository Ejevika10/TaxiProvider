package com.modsen.reportservice.service;

import com.modsen.exceptionstarter.exception.ServiceUnavailableException;
import com.modsen.reportservice.client.driver.DriverClientService;
import com.modsen.reportservice.client.rating.RatingClientService;
import com.modsen.reportservice.client.ride.RideClientService;
import com.modsen.reportservice.dto.DriverResponseDto;
import com.modsen.reportservice.dto.RatingResponseDto;
import com.modsen.reportservice.dto.RideResponseDto;
import com.modsen.reportservice.mapper.DriverMapper;
import com.modsen.reportservice.mapper.RideMapper;
import com.modsen.reportservice.model.DriverForReport;
import com.modsen.reportservice.model.RideForReport;
import com.modsen.reportservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.modsen.reportservice.util.AppConstants.REPORT_NAME;
import static com.modsen.reportservice.util.AppConstants.SUBREPORT_NAME;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DriverClientService driverClientService;
    private final RideClientService rideClientService;
    private final RatingClientService ratingClientService;
    private final RideMapper rideMapper;
    private final DriverMapper driverMapper;

    @Value(REPORT_NAME)
    private String reportName;

    @Value(SUBREPORT_NAME)
    private String subReportName;

    @Override
    public byte[] createReport(String driverId, String bearerToken) {
        DriverForReport driverForReport = driverMapper.toDriverForReport(
                getDriverById(driverId, bearerToken));

        LocalDateTime rideDateTime = LocalDateTime.now().minusMonths(1);
        List<RideResponseDto> rides = getRidesByDriverIdAndLocalDateTime(driverId, rideDateTime, bearerToken);
        List<Long> rideIds = rides.stream()
                .map(RideResponseDto::id)
                .toList();
        List<RatingResponseDto> ratings = getRatingsByRideIdIn(rideIds, bearerToken);

        List<RideForReport> ridesForReport = rideMapper.toRideForReportList(rides, ratings);

        return fillInReport(driverForReport, ridesForReport);
    }

    private byte[] fillInReport(DriverForReport driver, List<RideForReport> rides) {
        JasperReport report = getReport(reportName);

        Map<String, Object> parameters = collectParameters(rides, driver);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rides);

        return fillReport(report, parameters, dataSource);
    }

    private DriverResponseDto getDriverById(String driverId, String bearerToken) {
        return driverClientService.getDriverById(driverId, bearerToken);
    }

    private List<RideResponseDto> getRidesByDriverIdAndLocalDateTime(String driverId, LocalDateTime localDateTime, String bearerToken) {
        return rideClientService.getRidesByDriverIdAndLocalDateTime(driverId, localDateTime, bearerToken);
    }

    private List<RatingResponseDto> getRatingsByRideIdIn(List<Long> rideIds, String bearerToken) {
        return ratingClientService.getAllRatingsByRideIdIn(rideIds, bearerToken);
    }

    private JasperReport getReport(String template) {
        try {
            File file = ResourceUtils.getFile(template);
            return JasperCompileManager.compileReport(file.getAbsolutePath());
        } catch (FileNotFoundException | JRException ex) {
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }

    private byte[] fillReport(JasperReport report, Map<String, Object> parameters, JRBeanCollectionDataSource dataSource) {
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException ex) {
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }

    private Map<String, Object> collectParameters(List<RideForReport> rides, DriverForReport driver) {
        JRBeanCollectionDataSource subDataSource = new JRBeanCollectionDataSource(rides);

        Map<String, Object> subParameters = new HashMap<>();
        subParameters.put("title", "Rides for last month");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("driverName", driver.getName());
        parameters.put("driverEmail", driver.getEmail());
        parameters.put("driverPhone", driver.getPhone());
        parameters.put("driverRating", driver.getRating());
        parameters.put("subParameters", subParameters);
        parameters.put("subDataSource", subDataSource);
        parameters.put("subReport", getReport(subReportName));

        return parameters;
    }
}
