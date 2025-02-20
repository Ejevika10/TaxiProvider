package com.modsen.passengerservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.message.ListErrorMessage;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.modsen.passengerservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INVALID_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.LIMIT;
import static com.modsen.passengerservice.util.TestData.LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.OFFSET;
import static com.modsen.passengerservice.util.TestData.OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.PAGE_NUMBER;
import static com.modsen.passengerservice.util.TestData.PAGE_SIZE;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.PASSENGER_SCRIPT;
import static com.modsen.passengerservice.util.TestData.UNIQUE_EMAIL;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.TestData.getPagePassengerResponseDto;
import static com.modsen.passengerservice.util.TestData.getPassengerCreateRequestDtoBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDto;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDtoBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.passengerservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.passengerservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_EMAIL_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_EMAIL_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_ID_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_NAME_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_NAME_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_PHONE_INVALID;
import static com.modsen.passengerservice.util.ViolationData.PASSENGER_PHONE_MANDATORY;
import static com.modsen.passengerservice.util.ViolationData.UUID_INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = PASSENGER_SCRIPT,
        executionPhase = BEFORE_TEST_METHOD
)
public class PassengerControllerIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3");

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:management");

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        postgreSQLContainer.start();
        rabbitMQContainer.start();
    }

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    void getPagePassengers_whenEmptyParams_thenReturns200() {
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
    void getPagePassengers_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
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

        PageDto<PassengerResponseDto> actualPagePassengerResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<PassengerResponseDto>>() {});
        assertEquals(getPagePassengerResponseDto(), actualPagePassengerResponseDto);
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
    void getPassenger_whenValidId_thenReturns200AndResponseDto() throws Exception {
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
    void getPassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_ID, INVALID_PASSENGER_ID)
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
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDtoBuilder()
                .email(UNIQUE_EMAIL)
                .build();
        PassengerResponseDto expectedPassengerResponseDto = getPassengerResponseDtoBuilder()
                .id(PASSENGER_ID)
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
        PassengerCreateRequestDto passengerRequestDto = getEmptyPassengerCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_MANDATORY, PASSENGER_PHONE_MANDATORY, PASSENGER_NAME_MANDATORY, PASSENGER_EMAIL_MANDATORY));

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
        PassengerCreateRequestDto driverRequestDto = getInvalidPassengerCreateRequestDto();
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
    void updatePassenger_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();

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
        PassengerUpdateRequestDto passengerRequestDto = getEmptyPassengerUpdateRequestDto();
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
        PassengerUpdateRequestDto driverRequestDto = getInvalidPassengerUpdateRequestDto();
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
    void updatePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        PassengerUpdateRequestDto driverRequestDto = getPassengerUpdateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_PASSENGER_ID, INVALID_PASSENGER_ID)
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
    void deletePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        String responseJson = RestAssuredMockMvc
                .when()
                .delete(URL_PASSENGER_ID, INVALID_PASSENGER_ID)
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
