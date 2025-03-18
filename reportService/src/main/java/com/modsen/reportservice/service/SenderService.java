package com.modsen.reportservice.service;

import org.springframework.core.io.InputStreamSource;

public interface SenderService {

    void sendReport(String toAddress, String subject, String message,
                           String reportName, InputStreamSource reportInputStream);

}
