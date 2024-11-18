package com.modsen.passengerservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.exception.ListErrorMessage;
import com.modsen.passengerservice.repository.PassengerRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static com.modsen.passengerservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.LIMIT;
import static com.modsen.passengerservice.util.TestData.LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.OFFSET;
import static com.modsen.passengerservice.util.TestData.OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.PAGE_NUMBER;
import static com.modsen.passengerservice.util.TestData.PAGE_SIZE;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.UNIQUE_EMAIL;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassenger;
import static com.modsen.passengerservice.util.TestData.getPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerRequestDtoBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDto;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDtoBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDtoList;
import static com.modsen.passengerservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.passengerservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.passengerservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_EMAIL_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_EMAIL_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_ID_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_NAME_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_NAME_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_PHONE_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_PHONE_MANDATORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerControllerIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapper objectMapper;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        passengerRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE passenger_id_seq RESTART WITH 1");

        passengerRepository.save(getPassenger());
    }

    @Test
    void getPagePassengers_whenEmptyParams_thenReturns201() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPagePassengers_whenValidParams_thenReturns201AndResponseDto() {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        List<Map<String, Object>> content = JsonPath.parse(responseJson).read("$.content");
        List<PassengerResponseDto> actualPassengerResponseDtoList = objectMapper.convertValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PassengerResponseDto.class));
        assertEquals(getPassengerResponseDtoList(), actualPassengerResponseDtoList);
    }

    @Test
    void getPagePassengers_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER)
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
    void getPagePassengers_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPassenger_whenValidId_thenReturns201AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        PassengerResponseDto actualPassengerResponseDto = objectMapper.readValue(responseJson, PassengerResponseDto.class);

        assertEquals(getPassengerResponseDto(), actualPassengerResponseDto);
    }

    @Test
    void getPassenger_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actual = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actual);
    }

    @Test
    void createPassenger_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDtoBuilder()
                .email(UNIQUE_EMAIL)
                .build();
        PassengerResponseDto expectedPassengerResponseDto = getPassengerResponseDtoBuilder()
                .id(2L)
                .email(UNIQUE_EMAIL)
                .build();
        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(passengerRequestDto)
                .when()
                .post(URL_PASSENGER)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        PassengerResponseDto actualPassengerResponseDto = objectMapper.readValue(responseJson, PassengerResponseDto.class);

        assertEquals(expectedPassengerResponseDto, actualPassengerResponseDto);
    }

    @Test
    void createPassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto passengerRequestDto = getEmptyPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_MANDATORY, PASSENGER_NAME_MANDATORY, PASSENGER_EMAIL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(passengerRequestDto)
                .when()
                .post(URL_PASSENGER)
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
    void createPassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto driverRequestDto = getInvalidPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_INVALID, PASSENGER_NAME_INVALID, PASSENGER_EMAIL_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .post(URL_PASSENGER)
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
    void updatePassenger_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(passengerRequestDto)
                .when()
                .put(URL_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        PassengerResponseDto actualPassengerResponseDto = objectMapper.readValue(responseJson, PassengerResponseDto.class);

        assertEquals(getPassengerResponseDto(), actualPassengerResponseDto);
    }

    @Test
    void updatePassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto passengerRequestDto = getEmptyPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_MANDATORY, PASSENGER_NAME_MANDATORY, PASSENGER_EMAIL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(passengerRequestDto)
                .when()
                .put(URL_PASSENGER_ID, PASSENGER_ID.toString())
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
    void updatePassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto driverRequestDto = getInvalidPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_INVALID, PASSENGER_NAME_INVALID, PASSENGER_EMAIL_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_PASSENGER_ID, PASSENGER_ID.toString())
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
    void updatePassenger_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto driverRequestDto = getPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID.toString())
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
    void deletePassenger_whenValidId_thenReturns204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deletePassenger_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .when()
                .delete(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID.toString())
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
