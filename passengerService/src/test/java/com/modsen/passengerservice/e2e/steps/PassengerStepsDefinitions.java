package com.modsen.passengerservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static com.modsen.passengerservice.util.E2ETestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.E2ETestData.URL_PASSENGER_ID;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassengerStepsDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Response response;
    private PassengerRequestDto passengerRequestDto;

    @Given("Passenger request dto")
    public void passengerRequestDto(String requestBody) throws JsonProcessingException {
        passengerRequestDto = objectMapper.readValue(requestBody, PassengerRequestDto.class);
    }

    @When("Get page of passengers")
    public void getPageOfPassengers() {
        response = given()
                .when()
                .get(URL_PASSENGER);
    }

    @When("Get passenger by id {long}")
    public void getPassengerById(long id) {
        response = given()
                .when()
                .get(URL_PASSENGER_ID, id);
    }

    @When("Create passenger")
    public void createPassenger() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(passengerRequestDto)
                .when()
                .post(URL_PASSENGER);
    }

    @When("Update passenger with id {long}")
    public void updatePassengerWithId(long id) {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(passengerRequestDto)
                .when()
                .put(URL_PASSENGER_ID, id);
    }

    @When("Delete passenger with id {long}")
    public void deletePassengerWithId(long id) {
        response = given()
                .when()
                .delete(URL_PASSENGER_ID, id);
    }

    @Then("Response status is {int}")
    public void responseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Passenger response dto")
    public void responseBodyContainsPassengerResponseDto(String responseBody) throws JsonProcessingException {
        PassengerResponseDto expected = objectMapper.readValue(responseBody, PassengerResponseDto.class);
        PassengerResponseDto actual = response.body()
                .as(PassengerResponseDto.class);

        assertEquals(expected, actual);
    }

    @And("Response body contains Page dto")
    public void responseBodyContainsPageDto(String responseBody) throws JsonProcessingException {
        PageDto expected = objectMapper.readValue(responseBody, PageDto.class);
        PageDto actual = response.body()
                .as(PageDto.class);

        assertEquals(expected, actual);
    }
}
