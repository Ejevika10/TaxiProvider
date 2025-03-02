package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidateFileService {
    @Value("${minio.file.allowed-types}")
    private String allowedTypes;

    private List<String> allowedTypesList() {
        return Arrays.asList(allowedTypes.split(","));
    }

    public void validateFile(MultipartFile file) {
        if (!allowedTypesList().contains(file.getContentType())) {
            throw new InvalidFieldValueException(MessageConstants.INVALID_FILE_TYPE);
        }
    }
}
