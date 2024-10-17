package com.modsen.rideservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.exception.ClientException;
import com.modsen.rideservice.exception.ErrorMessage;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        ErrorMessage errorMessage;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            errorMessage = mapper.readValue(bodyIs, ErrorMessage.class);
        } catch (Exception e) {
            return new Exception(e.getMessage());
        }
        return new ClientException(errorMessage);
    }
}
