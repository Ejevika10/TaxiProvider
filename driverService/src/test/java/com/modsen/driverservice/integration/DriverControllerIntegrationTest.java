package com.modsen.driverservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.exception.ListErrorMessage;
import com.modsen.driverservice.repository.DriverRepository;
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

import static com.modsen.driverservice.util.TestData.DRIVER_ID;
import static com.modsen.driverservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.LIMIT;
import static com.modsen.driverservice.util.TestData.LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.OFFSET;
import static com.modsen.driverservice.util.TestData.OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.PAGE_NUMBER;
import static com.modsen.driverservice.util.TestData.PAGE_SIZE;
import static com.modsen.driverservice.util.TestData.UNIQUE_EMAIL;
import static com.modsen.driverservice.util.TestData.URL_DRIVER;
import static com.modsen.driverservice.util.TestData.URL_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.getDriver;
import static com.modsen.driverservice.util.TestData.getDriverRequestDto;
import static com.modsen.driverservice.util.TestData.getDriverRequestDtoBuilder;
import static com.modsen.driverservice.util.TestData.getDriverResponseDto;
import static com.modsen.driverservice.util.TestData.getDriverResponseDtoBuilder;
import static com.modsen.driverservice.util.TestData.getDriverResponseDtoList;
import static com.modsen.driverservice.util.TestData.getEmptyDriverRequestDto;
import static com.modsen.driverservice.util.TestData.getInvalidDriverRequestDto;
import static com.modsen.driverservice.util.ViolationData.DRIVER_EMAIL_INVALID;
import static com.modsen.driverservice.util.ViolationData.DRIVER_EMAIL_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.DRIVER_NAME_INVALID;
import static com.modsen.driverservice.util.ViolationData.DRIVER_NAME_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.DRIVER_PHONE_INVALID;
import static com.modsen.driverservice.util.ViolationData.DRIVER_PHONE_MANDATORY;
import static com.modsen.driverservice.util.ViolationData.ID_INVALID;
import static com.modsen.driverservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.driverservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.driverservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private DriverRepository driverRepository;

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
        driverRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE driver_id_seq RESTART WITH 1");

        driverRepository.save(getDriver());
    }

    @Test
    void getPageDrivers_whenEmptyParams_thenReturns201() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_DRIVER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageDrivers_whenValidParams_thenReturns201AndResponseDto() {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_DRIVER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        List<Map<String, Object>> content = JsonPath.parse(responseJson).read("$.content");
        List<DriverResponseDto> actualDriverResponseDtoList = objectMapper.convertValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, DriverResponseDto.class));
        assertEquals(getDriverResponseDtoList(), actualDriverResponseDtoList);
    }

    @Test
    void getPageDrivers_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_DRIVER)
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
    void getPageDrivers_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_DRIVER)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getDriver_whenValidId_thenReturns201AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        DriverResponseDto actualDriverResponseDto = objectMapper.readValue(responseJson, DriverResponseDto.class);

        assertEquals(getDriverResponseDto(), actualDriverResponseDto);
    }

    @Test
    void getDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actual = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actual);
    }

    @Test
    void createDriver_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDtoBuilder()
                .email(UNIQUE_EMAIL)
                .build();
        DriverResponseDto expectedDriverResponseDto = getDriverResponseDtoBuilder()
                .id(2L)
                .email(UNIQUE_EMAIL)
                .build();
        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .post(URL_DRIVER)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        DriverResponseDto actualDriverResponseDto = objectMapper.readValue(responseJson, DriverResponseDto.class);

        assertEquals(expectedDriverResponseDto, actualDriverResponseDto);
    }

    @Test
    void createDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getEmptyDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_MANDATORY, DRIVER_NAME_MANDATORY, DRIVER_EMAIL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .post(URL_DRIVER)
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
    void createDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getInvalidDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_INVALID, DRIVER_NAME_INVALID, DRIVER_EMAIL_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .post(URL_DRIVER)
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
    void updateDriver_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        DriverResponseDto actualDriverResponseDto = objectMapper.readValue(responseJson, DriverResponseDto.class);

        assertEquals(getDriverResponseDto(), actualDriverResponseDto);
    }

    @Test
    void updateDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getEmptyDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_MANDATORY, DRIVER_NAME_MANDATORY, DRIVER_EMAIL_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_DRIVER_ID, DRIVER_ID.toString())
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
    void updateDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getInvalidDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_INVALID, DRIVER_NAME_INVALID, DRIVER_EMAIL_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_DRIVER_ID, DRIVER_ID.toString())
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
    void updateDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(driverRequestDto)
                .when()
                .put(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID.toString())
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
    void deleteDriver_whenValidId_thenReturns204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .when()
                .delete(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID.toString())
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
