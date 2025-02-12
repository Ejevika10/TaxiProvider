package com.modsen.ratingservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.exception.ClientException;
import com.modsen.exceptionstarter.exception.ServiceUnavailableException;
import com.modsen.exceptionstarter.message.ErrorMessage;
import com.modsen.ratingservice.util.AppConstants;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class ExternalErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String errorString, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(bodyIs, ErrorMessage.class);
            log.info("ClientException");
            return new ClientException(errorMessage);
        } catch (Exception e) {
            log.info("Exception");
            log.info(e.getMessage());
            return new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
        }
    }
}
