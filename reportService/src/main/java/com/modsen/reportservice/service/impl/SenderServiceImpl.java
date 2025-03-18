package com.modsen.reportservice.service.impl;

import com.modsen.exceptionstarter.exception.ServiceUnavailableException;
import com.modsen.reportservice.service.SenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.modsen.reportservice.util.MessageConstants.SERVICE_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {

    private final JavaMailSender mailSender;

    @Override
    public void sendReport(String toAddress, String subject, String message,
                           String reportName, InputStreamSource reportInputStream) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(reportName, reportInputStream);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ServiceUnavailableException(SERVICE_UNAVAILABLE);
        }

    }
}
