package com.modsen.driverservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.DriverUpdateRequestDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.e2e.dto.LoginResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static com.modsen.driverservice.util.E2ETestData.URL_CAR;
import static com.modsen.driverservice.util.E2ETestData.URL_CAR_DRIVER_ID;
import static com.modsen.driverservice.util.E2ETestData.URL_CAR_ID;
import static com.modsen.driverservice.util.E2ETestData.URL_DRIVER;
import static com.modsen.driverservice.util.E2ETestData.URL_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.AUTHORIZATION;
import static com.modsen.driverservice.util.TestData.BEARER;
import static com.modsen.driverservice.util.TestData.URL_AUTHENTICATION;
import static com.modsen.driverservice.util.TestData.getLoginRequestDto;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverStepsDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Response response;
    private String accessToken;
    private DriverCreateRequestDto driverCreateRequestDto;
    private DriverUpdateRequestDto driverUpdateRequestDto;
    private CarRequestDto carRequestDto;

    @Given("Access token")
    public void accessToken() throws JsonProcessingException{
        Response response = given()
                .contentType(ContentType.JSON)
                .body(getLoginRequestDto())
                .when()
                .post(URL_AUTHENTICATION);
        LoginResponseDto loginResponse = response.body().as(LoginResponseDto.class);
        accessToken = loginResponse.accessToken();
    }

    @Given("Car request dto")
    public void carRequestDto(String requestBody) throws JsonProcessingException {
        carRequestDto = objectMapper.readValue(requestBody, CarRequestDto.class);
    }

    @When("Get page of cars")
    public void getPageOfCars() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_CAR);
    }

    @When("Get page of cars by driver id {string}")
    public void getPageOfCarsByDriverId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_CAR_DRIVER_ID, id);
    }

    @When("Get car by id {long}")
    public void getCarById(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_CAR_ID, id);
    }

    @When("Create car")
    public void createCar() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(carRequestDto)
                .when()
                .post(URL_CAR);
    }

    @When("Update car with id {long}")
    public void updateCarWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, id);
    }

    @When("Delete car with id {long}")
    public void deleteCarWithId(long id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .delete(URL_CAR_ID, id);
    }

    @And("Response body contains Car response dto")
    public void responseBodyContainsCarResponseDto(String responseBody) throws JsonProcessingException {
        CarResponseDto expected = objectMapper.readValue(responseBody, CarResponseDto.class);
        CarResponseDto actual = response.body()
                .as(CarResponseDto.class);

        assertEquals(expected, actual);
    }

    @Given("Driver create request dto")
    public void driverCreateRequestDto(String requestBody) throws JsonProcessingException {
        driverCreateRequestDto = objectMapper.readValue(requestBody, DriverCreateRequestDto.class);
    }

    @Given("Driver update request dto")
    public void driverUpdateRequestDto(String requestBody) throws JsonProcessingException {
        driverUpdateRequestDto = objectMapper.readValue(requestBody, DriverUpdateRequestDto.class);
    }

    @When("Get page of drivers")
    public void getPageOfDrivers() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_DRIVER);
    }

    @When("Get driver by id {string}")
    public void getDriverById(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_DRIVER_ID, id);
    }

    @When("Create driver")
    public void createDriver() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(driverCreateRequestDto)
                .when()
                .post(URL_DRIVER);
    }

    @When("Update driver with id {string}")
    public void updateDriverWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(driverUpdateRequestDto)
                .when()
                .put(URL_DRIVER_ID, id);
    }

    @When("Delete driver with id {string}")
    public void deleteDriverWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .delete(URL_DRIVER_ID, id);
    }

    @Then("Response status is {int}")
    public void responseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Driver response dto")
    public void responseBodyContainsDriverResponseDto(String responseBody) throws JsonProcessingException {
        DriverResponseDto expected = objectMapper.readValue(responseBody, DriverResponseDto.class);
        DriverResponseDto actual = response.body()
                .as(DriverResponseDto.class);

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
