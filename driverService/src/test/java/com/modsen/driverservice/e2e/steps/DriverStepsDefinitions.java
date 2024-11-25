package com.modsen.driverservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.modsen.driverservice.util.E2ETestData.URL_CAR;
import static com.modsen.driverservice.util.E2ETestData.URL_CAR_DRIVER_ID;
import static com.modsen.driverservice.util.E2ETestData.URL_CAR_ID;
import static com.modsen.driverservice.util.E2ETestData.URL_DRIVER;
import static com.modsen.driverservice.util.E2ETestData.URL_DRIVER_ID;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverStepsDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Response response;
    private DriverRequestDto driverRequestDto;
    private CarRequestDto carRequestDto;


    @Given("Car request dto")
    public void carRequestDto(String requestBody) throws JsonProcessingException {
        carRequestDto = objectMapper.readValue(requestBody, CarRequestDto.class);
    }

    @When("Get page of cars")
    public void getPageOfCars() {
        response = given()
                .when()
                .get(URL_CAR);
    }

    @When("Get page of cars by driver id {long}")
    public void getPageOfCarsByDriverId(long id) {
        response = given()
                .when()
                .get(URL_CAR_DRIVER_ID, id);
    }

    @When("Get car by id {long}")
    public void GetCarById(long id) {
        response = given()
                .when()
                .get(URL_CAR_ID, id);
    }

    @When("Create car")
    public void createCar() {
        response = given()
                .contentType("application/json")
                .body(carRequestDto)
                .when()
                .post(URL_CAR);
    }

    @When("Update car with id {long}")
    public void updateCarWithId(long id) {
        response = given()
                .contentType("application/json")
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, id);
    }

    @When("Delete car with id {long}")
    public void deleteCarWithId(long id) {
        response = given()
                .when()
                .delete(URL_CAR_ID, id);
    }

    @And("Response body contains Car response dto")
    public void responseBodyContainsCarResponseDto(String responseBody) throws JsonProcessingException {
        CarResponseDto expected = objectMapper.readValue(responseBody, CarResponseDto.class);
        CarResponseDto actual = response.body().as(CarResponseDto.class);

        assertEquals(expected, actual);
    }

    @Given("Driver request dto")
    public void driverRequestDto(String requestBody) throws JsonProcessingException {
        driverRequestDto = objectMapper.readValue(requestBody, DriverRequestDto.class);
    }

    @When("Get page of drivers")
    public void getPageOfDrivers() {
        response = given()
                .when()
                .get(URL_DRIVER);
    }

    @When("Get driver by id {long}")
    public void GetDriverById(long id) {
        response = given()
                .when()
                .get(URL_DRIVER_ID, id);
    }

    @When("Create driver")
    public void createDriver() {
        response = given()
                .contentType("application/json")
                .body(driverRequestDto)
                .when()
                .post(URL_DRIVER);
    }

    @When("Update driver with id {long}")
    public void updateDriverWithId(long id) {
        response = given()
                .contentType("application/json")
                .body(driverRequestDto)
                .when()
                .put(URL_DRIVER_ID, id);
    }

    @When("Delete driver with id {long}")
    public void deleteDriverWithId(long id) {
        response = given()
                .when()
                .delete(URL_DRIVER_ID, id);
    }

    @Then("Response status is {int}")
    public void ResponseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Driver response dto")
    public void responseBodyContainsDriverResponseDto(String responseBody) throws JsonProcessingException {
        DriverResponseDto expected = objectMapper.readValue(responseBody, DriverResponseDto.class);
        DriverResponseDto actual = response.body().as(DriverResponseDto.class);

        assertEquals(expected, actual);
    }

    @And("Response body contains Page dto")
    public void responseBodyContainsPageDto(String responseBody) throws JsonProcessingException {
        PageDto expected = objectMapper.readValue(responseBody, PageDto.class);
        PageDto actual = response.body().as(PageDto.class);

        assertEquals(expected, actual);
    }
}
