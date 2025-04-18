package com.modsen.ratingservice.e2e.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.e2e.dto.LoginResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static com.modsen.ratingservice.util.E2ETestData.URL_DRIVER_RATING;
import static com.modsen.ratingservice.util.E2ETestData.URL_DRIVER_RATING_ID;
import static com.modsen.ratingservice.util.E2ETestData.URL_DRIVER_RATING_USER_ID;
import static com.modsen.ratingservice.util.E2ETestData.URL_PASSENGER_RATING;
import static com.modsen.ratingservice.util.E2ETestData.URL_PASSENGER_RATING_ID;
import static com.modsen.ratingservice.util.E2ETestData.URL_PASSENGER_RATING_USER_ID;
import static com.modsen.ratingservice.util.TestData.AUTHORIZATION;
import static com.modsen.ratingservice.util.TestData.BEARER;
import static com.modsen.ratingservice.util.TestData.URL_AUTHENTICATION;
import static com.modsen.ratingservice.util.TestData.getLoginRequestDto;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingStepsDefinitions {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Response response;
    private String accessToken;
    private RatingRequestDto ratingRequestDto;

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

    @Given("Rating request dto")
    public void ratingRequestDto(String requestBody) throws JsonProcessingException {
        ratingRequestDto = objectMapper.readValue(requestBody, RatingRequestDto.class);
    }

    @When("Get page of passenger ratings")
    public void getPageOfPassengerRatings() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_PASSENGER_RATING);
    }

    @When("Get page of driver ratings")
    public void getPageOfDriverRatings() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_DRIVER_RATING);
    }

    @When("Get page of passenger ratings by user id {string}")
    public void getPageOfPassengerRatingsByUserId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, id);
    }

    @When("Get page of driver ratings by user id {string}")
    public void getPageOfDriverRatingsByUserId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_DRIVER_RATING_USER_ID, id);
    }

    @When("Get passenger rating by id {string}")
    public void getPassengerRatingById(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_PASSENGER_RATING_ID, id);
    }

    @When("Get driver rating by id {string}")
    public void getDriverRatingById(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(URL_DRIVER_RATING_ID, id);
    }

    @When("Create passenger rating")
    public void createPassengerRating() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING);
    }

    @When("Create driver rating")
    public void createDriverRating() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ratingRequestDto)
                .when()
                .post(URL_DRIVER_RATING);
    }

    @When("Update passenger rating with id {string}")
    public void updatePassengerRatingWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, id);
    }

    @When("Update driver rating with id {string}")
    public void updateDriverRatingWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ratingRequestDto)
                .when()
                .put(URL_DRIVER_RATING_ID, id);
    }

    @When("Delete passenger rating with id {string}")
    public void deletePassengerRatingWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .delete(URL_PASSENGER_RATING_ID, id);
    }

    @When("Delete driver rating with id {string}")
    public void deleteDriverRatingWithId(String id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .delete(URL_DRIVER_RATING_ID, id);
    }

    @Then("Response status is {int}")
    public void responseStatus(int status) {
        response
                .then()
                .statusCode(status);
    }

    @And("Response body contains Rating response dto")
    public void responseBodyContainsRideResponseDto(String responseBody) throws JsonProcessingException {
        RatingResponseDto expected = objectMapper.readValue(responseBody, RatingResponseDto.class);
        RatingResponseDto actual = response.body()
                .as(RatingResponseDto.class);

        assertEqualsWithoutId(expected, actual);
    }

    @And("Response body contains Page dto")
    public void responseBodyContainsPageDto(String responseBody) throws JsonProcessingException {
        PageDto expected = objectMapper.readValue(responseBody, PageDto.class);
        PageDto actual = response.body()
                .as(PageDto.class);

        assertEqualsForPageWithoutId(expected, actual);
    }

    private void assertEqualsWithoutId(RatingResponseDto expected, RatingResponseDto actual) {
        assertEquals(expected.rideId(), actual.rideId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.rating(), actual.rating());
        assertEquals(expected.comment(), actual.comment());
    }

    private void assertEqualsForPageWithoutId(PageDto expectedPage, PageDto actualPage) {
        assertEquals(expectedPage.pageNumber(), actualPage.pageNumber());
        assertEquals(expectedPage.pageSize(), actualPage.pageSize());
        assertEquals(expectedPage.totalPages(), actualPage.totalPages());
        assertEquals(expectedPage.totalElements(), actualPage.totalElements());
        for(int i = 0; i < expectedPage.content().size(); i++) {
            RatingResponseDto expected = objectMapper.convertValue(expectedPage.content().get(i), RatingResponseDto.class);
            RatingResponseDto actual = objectMapper.convertValue(actualPage.content().get(i), RatingResponseDto.class);
            assertEqualsWithoutId(expected, actual);
        }
    }
}
