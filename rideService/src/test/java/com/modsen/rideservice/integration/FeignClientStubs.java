package com.modsen.rideservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.modsen.rideservice.exception.ErrorMessage;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.modsen.rideservice.util.TestData.DRIVER_NOT_FOUND;
import static com.modsen.rideservice.util.TestData.PASSENGER_NOT_FOUND;
import static com.modsen.rideservice.util.TestData.URL_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.URL_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.getDriverResponseDto;
import static com.modsen.rideservice.util.TestData.getPassengerResponseDto;

@Component
@PropertySource("classpath:test.properties")
public class FeignClientStubs {

    private final ObjectMapper objectMapper;

    public FeignClientStubs(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void stubForPassengerServiceWithExistingPassenger(Long userId, WireMockExtension PASSENGER_SERVICE) throws Exception {
        PASSENGER_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_PASSENGER_ID + userId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(getPassengerResponseDto()))));
    }

    public void stubForPassengerServiceWithNonExistingPassenger(Long userId, WireMockExtension PASSENGER_SERVICE) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), PASSENGER_NOT_FOUND);
        PASSENGER_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_PASSENGER_ID + userId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(errorMessage))
                ));
    }

    public void stubForDriverServiceWithExistingDriver(Long userId, WireMockExtension DRIVER_SERVICE) throws Exception {
        DRIVER_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_DRIVER_ID + userId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(getDriverResponseDto()))
                ));
    }

    public void stubForDriverServiceWithNonExistingDriver(Long userId, WireMockExtension DRIVER_SERVICE) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), DRIVER_NOT_FOUND);
        DRIVER_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_DRIVER_ID + userId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(errorMessage))
                ));
    }
}