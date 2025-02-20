package com.modsen.ratingservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.modsen.exceptionstarter.message.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.modsen.ratingservice.util.TestData.RIDE_NOT_FOUND;
import static com.modsen.ratingservice.util.TestData.URL_RIDES;
import static com.modsen.ratingservice.util.TestData.getRideResponseDto;

@Component
public class FeignClientStubs {

    private final ObjectMapper objectMapper;

    public FeignClientStubs(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void stubForRideServiceWithExistingRide(Long rideId, WireMockExtension RIDE_SERVICE) throws Exception {
        RIDE_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_RIDES + rideId.toString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(getRideResponseDto()))
                ));
    }

    public void stubForRideServiceWithNonExistingRide(Long rideId, WireMockExtension RIDE_SERVICE) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), RIDE_NOT_FOUND);
        RIDE_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_RIDES + rideId.toString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(errorMessage))
                ));
    }
}
