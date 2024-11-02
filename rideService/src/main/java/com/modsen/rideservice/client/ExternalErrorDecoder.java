package com.modsen.rideservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.exception.ClientException;
import com.modsen.rideservice.exception.ErrorMessage;
import com.modsen.rideservice.exception.ServiceUnavailableException;
import com.modsen.rideservice.util.AppConstants;
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
            log.info("Exception");
            log.info(e.getMessage());
            return new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
        }
    }
}
