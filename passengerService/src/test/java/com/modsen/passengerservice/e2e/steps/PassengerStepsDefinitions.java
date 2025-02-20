package com.modsen.passengerservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.e2e.dto.LoginResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static com.modsen.passengerservice.util.E2ETestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.E2ETestData.URL_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.AUTHORIZATION;
import static com.modsen.passengerservice.util.TestData.BEARER;
import static com.modsen.passengerservice.util.TestData.URL_AUTHENTICATION;
import static com.modsen.passengerservice.util.TestData.getLoginRequestDto;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassengerStepsDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Response response;
    private String accessToken;
    private PassengerCreateRequestDto passengerCreateRequestDto;
    private PassengerUpdateRequestDto passengerUpdateRequestDto;

    @Given("Access token")
    public void accessToken() throws JsonProcessingException {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(getLoginRequestDto())
                .when()
                .post(URL_AUTHENTICATION);

        LoginResponseDto loginResponse = response.as(LoginResponseDto.class);
        accessToken = loginResponse.accessToken();
    }

    @Given("Passenger create request dto")
    public void passengerCreateRequestDto(String requestBody) throws JsonProcessingException {
        passengerCreateRequestDto = objectMapper.readValue(requestBody, PassengerCreateRequestDto.class);
    }

    @Given("Passenger update request dto")
    public void passengerUpdateRequestDto(String requestBody) throws JsonProcessingException {
        passengerUpdateRequestDto = objectMapper.readValue(requestBody, PassengerUpdateRequestDto.class);
    }

    @When("Get page of passengers")
    public void getPageOfPassengers() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_PASSENGER);
    }

    @When("Get passenger by id {string}")
    public void getPassengerById(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_PASSENGER_ID, id);
    }

    @When("Create passenger")
    public void createPassenger() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(passengerCreateRequestDto)
                .when()
                .post(URL_PASSENGER);
    }

    @When("Update passenger with id {string}")
    public void updatePassengerWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(passengerUpdateRequestDto)
                .when()
                .put(URL_PASSENGER_ID, id);
    }

    @When("Delete passenger with id {string}")
    public void deletePassengerWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
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
