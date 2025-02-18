package com.modsen.rideservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.e2e.dto.LoginResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static com.modsen.rideservice.util.E2ETestData.URL_RIDE;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_DRIVER_ID;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_ID;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_ID_ACCEPT;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_ID_CANCEL;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_ID_STATE;
import static com.modsen.rideservice.util.E2ETestData.URL_RIDE_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.AUTHORIZATION;
import static com.modsen.rideservice.util.TestData.BEARER;
import static com.modsen.rideservice.util.TestData.URL_AUTHENTICATION;
import static com.modsen.rideservice.util.TestData.getLoginRequestDto;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RideStepsDefinitions {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    private Response response;
    private String accessToken;
    private RideCreateRequestDto rideCreateRequestDto;
    private RideAcceptRequestDto rideAcceptRequestDto;
    private RideRequestDto rideRequestDto;
    private RideStateRequestDto rideStateRequestDto;

    @Given("Access token")
    public void accessToken() throws JsonProcessingException {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(getLoginRequestDto())
                .when()
                .post(URL_AUTHENTICATION);

        LoginResponseDto loginResponse = response.as(LoginResponseDto.class);
        accessToken = loginResponse.access_token();
    }

    @Given("Ride create request dto")
    public void rideCreateRequestDto(String requestBody) throws JsonProcessingException {
        rideCreateRequestDto = objectMapper.readValue(requestBody, RideCreateRequestDto.class);
    }

    @Given("Ride accept request dto")
    public void rideAcceptRequestDto(String requestBody) throws JsonProcessingException {
        rideAcceptRequestDto = objectMapper.readValue(requestBody, RideAcceptRequestDto.class);
    }

    @Given("Ride request dto")
    public void rideRequestDto(String requestBody) throws JsonProcessingException {
        rideRequestDto = objectMapper.readValue(requestBody, RideRequestDto.class);
    }

    @Given("Ride state request dto")
    public void rideStateRequestDto(String requestBody) throws JsonProcessingException {
        rideStateRequestDto = objectMapper.readValue(requestBody, RideStateRequestDto.class);
    }

    @When("Get page of rides")
    public void getPageOfRides() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_RIDE);
    }

    @When("Get page of rides by driver id {string}")
    public void getPageOfRidesByDriverId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_RIDE_DRIVER_ID, id);
    }

    @When("Get page of rides by passenger id {string}")
    public void getPageOfRidesByPassengerId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_RIDE_PASSENGER_ID, id);
    }

    @When("Get ride by id {long}")
    public void getRideById(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_RIDE_ID, id);
    }

    @When("Create ride")
    public void createRide() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(rideCreateRequestDto)
                .when()
                .post(URL_RIDE);
    }

    @When("Update ride with id {long}")
    public void updateRideWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, id);
    }

    @When("Accept ride with id {long}")
    public void acceptRideWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, id);
    }

    @When("Cancel ride with id {long}")
    public void cancelRideWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .put(URL_RIDE_ID_CANCEL, id);
    }

    @When("Update ride state with id {long}")
    public void updateRideStateWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(rideStateRequestDto)
                .when()
                .put(URL_RIDE_ID_STATE, id);
    }

    @Then("Response status is {int}")
    public void responseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Ride response dto")
    public void responseBodyContainsRideResponseDto(String responseBody) throws JsonProcessingException {
        RideResponseDto expected = objectMapper.readValue(responseBody, RideResponseDto.class);
        RideResponseDto actual = response.body()
                .as(RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(expected, actual);
    }

    @And("Response body contains Page dto")
    public void responseBodyContainsPageDto(String responseBody) throws JsonProcessingException {
        PageDto expected = objectMapper.readValue(responseBody, PageDto.class);
        PageDto actual = response.body()
                .as(PageDto.class);

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

    private void assertEqualsForPageWithoutTimeAndCost(PageDto expectedPage, PageDto actualPage) {
        assertEquals(expectedPage.pageNumber(), actualPage.pageNumber());
        assertEquals(expectedPage.pageSize(), actualPage.pageSize());
        assertEquals(expectedPage.totalPages(), actualPage.totalPages());
        assertEquals(expectedPage.totalElements(), actualPage.totalElements());
        for(int i = 0; i < expectedPage.content().size(); i++) {
            RideResponseDto expected = objectMapper.convertValue(expectedPage.content().get(i), RideResponseDto.class);
            RideResponseDto actual = objectMapper.convertValue(actualPage.content().get(i), RideResponseDto.class);
            assertEqualsWithoutTimeAndCost(expected, actual);
        }
    }
}
