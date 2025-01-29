package com.modsen.authservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.authservice.exception.ClientException;
import com.modsen.authservice.exception.ErrorMessage;
import com.modsen.authservice.exception.ServiceUnavailableException;
import com.modsen.authservice.util.AppConstants;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class ExternalErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(bodyIs, ErrorMessage.class);
            log.info("ClientException");
            return new ClientException(errorMessage);
        } catch (Exception e) {
            log.info("Exception {}", e.getMessage());
            return new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
        }
    }
}
