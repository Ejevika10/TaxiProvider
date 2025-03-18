package com.modsen.reportservice.controller;

import com.modsen.reportservice.service.ReportService;
import com.modsen.reportservice.service.SenderService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.modsen.reportservice.util.AppConstants.REPORT_FILE_NAME;
import static com.modsen.reportservice.util.AppConstants.REPORT_MESSAGE;
import static com.modsen.reportservice.util.AppConstants.REPORT_SUBJECT;
import static com.modsen.reportservice.util.AppConstants.UUID_REGEXP;

@RestController
@Validated
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController implements ReportEndpoints{

    private final ReportService reportService;
    private final SenderService senderService;

    @Override
    @GetMapping("/{driverId}")
    public ResponseEntity<Resource> getReport(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String driverId,
                                              @RequestParam @Email String email,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        byte[] pdfReport = reportService.createReport(driverId, bearerToken);

        ByteArrayResource resource = new ByteArrayResource(pdfReport);

        senderService.sendReport(email,
                REPORT_SUBJECT,
                REPORT_MESSAGE,
                REPORT_FILE_NAME,
                resource);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(REPORT_FILE_NAME)
                                .build()
                                .toString())
                .body(resource);
    }
}
