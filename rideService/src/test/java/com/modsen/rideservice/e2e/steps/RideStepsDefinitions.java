package com.modsen.rideservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.modsen.rideservice.util.E2ETestData.URL_RIDE;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_ID;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RideStepsDefinitions {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    private Response response;
    private RideRequestDto rideRequestDto;

    @Given("Ride request dto")
    public void passengerRequestDto(String requestBody) throws JsonProcessingException {
        rideRequestDto = objectMapper.readValue(requestBody, RideRequestDto.class);
    }

    @When("Get page of rides")
    public void getPageOfPassengers() {
        response = given()
                .when()
                .get(URL_RIDE);
    }

    @When("Get ride by id {long}")
    public void GetPassengerById(long id) {
        response = given()
                .when()
                .get(URL_RIDE_ID, id);
    }

    @When("Create ride")
    public void createPassenger() {
        response = given()
                .contentType("application/json")
                .body(rideRequestDto)
                .when()
                .post(URL_RIDE);
    }

    @When("Update ride with id {long}")
    public void updatePassengerWithId(long id) {
        response = given()
                .contentType("application/json")
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, id);
    }

    @When("Delete ride with id {long}")
    public void deletePassengerWithId(long id) {
        response = given()
                .when()
                .delete(URL_RIDE_ID, id);
    }

    @Then("Response status is {int}")
    public void ResponseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Ride response dto")
    public void responseBodyContainsPassengerResponseDto(String responseBody) throws JsonProcessingException {
        RideResponseDto actual = response.body().as(RideResponseDto.class);
        RideResponseDto expected = objectMapper.readValue(responseBody, RideResponseDto.class);

        assertEquals(expected, actual);
    }

    @And("Response body contains Page dto")
    public void responseBodyContainsPageDto(String responseBody) throws JsonProcessingException {
        PageDto<RideResponseDto> expected = objectMapper.readValue(responseBody, PageDto.class);
        PageDto<RideResponseDto> actual = response.body().as(PageDto.class);

        assertEqualsForPageWithoutTimeAndCost(expected, actual);
    }

    private void assertEqualsWithoutTimeAndCost(RideResponseDto expected, RideResponseDto actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.driverId(), actual.driverId());
        assertEquals(expected.passengerId(), actual.passengerId());
        assertEquals(expected.sourceAddress(), actual.sourceAddress());
        assertEquals(expected.destinationAddress(), actual.destinationAddress());
        assertEquals(expected.rideState(), actual.rideState());
    }

    private void assertEqualsForPageWithoutTimeAndCost(PageDto<RideResponseDto> expectedPage, PageDto<RideResponseDto> actualPage) {
        assertEquals(expectedPage.pageNumber(), actualPage.pageNumber());
        assertEquals(expectedPage.pageSize(), actualPage.pageSize());
        assertEquals(expectedPage.totalPages(), actualPage.totalPages());
        assertEquals(expectedPage.totalElements(), actualPage.totalElements());
        for(int i = 0; i < expectedPage.content().size(); i++) {
            assertEqualsWithoutTimeAndCost(expectedPage.content().get(i), actualPage.content().get(i));
        }
    }
}