package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.AvatarDto;
import com.modsen.driverservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.exceptionstarter.exception.ServiceUnavailableException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    private final ValidateFileService validateFileService;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public AvatarDto uploadImage(MultipartFile file, String newName) {
        validateFileService.validateFile(file);
        try {
            uploadFile(bucketName, file, newName, file.getContentType());
            return new AvatarDto(newName);
        } catch(Exception e) {
            log.info(e.getMessage());
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }

    private void uploadFile(String bucketName, MultipartFile file, String fileName, String contentType) throws Exception {
        bucketPrepare(bucketName);
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build());
    }

    public Resource downloadFile(String fileName) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return new InputStreamResource(inputStream);
        } catch (ErrorResponseException e) {
            log.info(e.getMessage());
            throw new NotFoundException(MessageConstants.AVATAR_NOT_FOUND);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }

    public String getFileContentType(String fileName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            ).contentType();
        } catch (Exception e) {
            log.info(e.getMessage());
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    private void bucketPrepare(String bucketName) {
        try {
            if(!bucketExists(bucketName)) {
                createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }

    private void createBucket(String bucketName) throws Exception {
        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    private boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build());
    }
}
