package com.modsen.ratingservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.ratingservice.exception.ClientException;
import com.modsen.ratingservice.exception.ErrorMessage;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ExternalErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String errorString, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(bodyIs, ErrorMessage.class);
            return new ClientException(errorMessage);
        } catch (Exception e) {
            return new Exception(e.getMessage());
        }
    }
}
