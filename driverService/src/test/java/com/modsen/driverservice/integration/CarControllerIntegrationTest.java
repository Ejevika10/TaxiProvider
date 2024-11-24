package com.modsen.driverservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.exception.ListErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.modsen.driverservice.util.TestData.CAR_ID;
import static com.modsen.driverservice.util.TestData.CAR_SCRIPT;
import static com.modsen.driverservice.util.TestData.DRIVER_ID;
import static com.modsen.driverservice.util.TestData.DRIVER_SCRIPT;
import static com.modsen.driverservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_CAR_ID;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.LIMIT;
import static com.modsen.driverservice.util.TestData.LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.OFFSET;
import static com.modsen.driverservice.util.TestData.OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.PAGE_NUMBER;
import static com.modsen.driverservice.util.TestData.PAGE_SIZE;
import static com.modsen.driverservice.util.TestData.UNIQUE_NUMBER;
import static com.modsen.driverservice.util.TestData.URL_CAR;
import static com.modsen.driverservice.util.TestData.URL_CAR_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.URL_CAR_ID;
import static com.modsen.driverservice.util.TestData.getCarRequestDto;
import static com.modsen.driverservice.util.TestData.getCarRequestDtoBuilder;
import static com.modsen.driverservice.util.TestData.getCarResponseDto;
import static com.modsen.driverservice.util.TestData.getCarResponseDtoBuilder;
import static com.modsen.driverservice.util.TestData.getEmptyCarRequestDto;
import static com.modsen.driverservice.util.TestData.getInvalidCarRequestDto;
import static com.modsen.driverservice.util.TestData.getPageCarResponseDto;
import static com.modsen.driverservice.util.ViolationData.CAR_BRAND_INVALID;
import static com.modsen.driverservice.util.ViolationData.CAR_BRAND_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.CAR_COLOR_INVALID;
import static com.modsen.driverservice.util.ViolationData.CAR_COLOR_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.CAR_MODEL_INVALID;
import static com.modsen.driverservice.util.ViolationData.CAR_MODEL_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.CAR_NUMBER_INVALID;
import static com.modsen.driverservice.util.ViolationData.CAR_NUMBER_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.DRIVER_ID_INVALID;
import static com.modsen.driverservice.util.ViolationData.ID_INVALID;
import static com.modsen.driverservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.driverservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.driverservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = {DRIVER_SCRIPT, CAR_SCRIPT},
        executionPhase = BEFORE_TEST_METHOD
)
public class CarControllerIntegrationTest extends ControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    void getPageCars_whenEmptyParams_thenReturns200() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_CAR)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageCars_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_CAR)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        PageDto<CarResponseDto> actualPageCarResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<CarResponseDto>>() {});
        assertEquals(getPageCarResponseDto(), actualPageCarResponseDto);
    }

    @Test
    void getPageCars_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_CAR)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageCars_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_CAR)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getCar_whenValidId_thenReturns200AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_CAR_ID, CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        CarResponseDto actualCarResponseDto = objectMapper.readValue(responseJson, CarResponseDto.class);

        assertEquals(getCarResponseDto(), actualCarResponseDto);
    }

    @Test
    void getCar_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_CAR_ID, INSUFFICIENT_CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actual = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actual);
    }

    @Test
    void getPageCarsByDriverId_whenEmptyParams_thenReturns200() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_CAR_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageCarsByDriverId_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_CAR_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        PageDto<CarResponseDto> actualPageCarResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<CarResponseDto>>() {});
        assertEquals(getPageCarResponseDto(), actualPageCarResponseDto);
    }

    @Test
    void getPageCarsByDriverId_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_CAR_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageCarsByDriverId_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_CAR_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageCarsByDriverId_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_CAR_DRIVER_ID, INSUFFICIENT_DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void createCar_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDtoBuilder()
                .number(UNIQUE_NUMBER)
                .build();
        CarResponseDto expectedCarResponseDto = getCarResponseDtoBuilder()
                .id(2L)
                .number(UNIQUE_NUMBER)
                .build();

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .post(URL_CAR)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        CarResponseDto actualCarResponseDto = objectMapper.readValue(responseJson, CarResponseDto.class);

        assertEquals(expectedCarResponseDto, actualCarResponseDto);
    }

    @Test
    void createCar_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto carRequestDto = getEmptyCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(CAR_BRAND_MANDATORY, CAR_COLOR_MANDATORY, CAR_NUMBER_MANDATORY, CAR_MODEL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .post(URL_CAR)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createCar_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto carRequestDto = getInvalidCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(CAR_BRAND_INVALID, CAR_COLOR_INVALID, CAR_MODEL_INVALID, CAR_NUMBER_INVALID, DRIVER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .post(URL_CAR)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateCar_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        CarResponseDto actualCarResponseDto = objectMapper.readValue(responseJson, CarResponseDto.class);

        assertEquals(getCarResponseDto(), actualCarResponseDto);
    }

    @Test
    void updateCar_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto carRequestDto = getEmptyCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(CAR_BRAND_MANDATORY, CAR_COLOR_MANDATORY, CAR_NUMBER_MANDATORY, CAR_MODEL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateCar_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto carRequestDto = getInvalidCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(CAR_BRAND_INVALID, CAR_COLOR_INVALID, CAR_MODEL_INVALID, CAR_NUMBER_INVALID, DRIVER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateCar_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(carRequestDto)
                .when()
                .put(URL_CAR_ID, INSUFFICIENT_CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deleteCar_whenValidId_thenReturns204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_CAR_ID, CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteCar_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .when()
                .delete(URL_CAR_ID, INSUFFICIENT_CAR_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}
