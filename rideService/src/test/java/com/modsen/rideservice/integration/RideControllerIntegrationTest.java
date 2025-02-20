package com.modsen.rideservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.modsen.exceptionstarter.message.ErrorMessage;
import com.modsen.exceptionstarter.message.ListErrorMessage;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.model.RideState;
import com.modsen.rideservice.util.TestServiceInstanceListSupplier;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static com.modsen.rideservice.util.TestData.AUTHORIZATION;
import static com.modsen.rideservice.util.TestData.AUTHORIZATION_VALUE;
import static com.modsen.rideservice.util.TestData.DRIVER_ID;
import static com.modsen.rideservice.util.TestData.DRIVER_NOT_FOUND;
import static com.modsen.rideservice.util.TestData.DRIVER_SERVICE_NAME;
import static com.modsen.rideservice.util.TestData.DRIVER_SERVICE_PORT;
import static com.modsen.rideservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_RIDE_ID;
import static com.modsen.rideservice.util.TestData.INVALID_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.INVALID_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.LIMIT;
import static com.modsen.rideservice.util.TestData.LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.OFFSET;
import static com.modsen.rideservice.util.TestData.OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.PAGE_NUMBER;
import static com.modsen.rideservice.util.TestData.PAGE_SIZE;
import static com.modsen.rideservice.util.TestData.PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.PASSENGER_NOT_FOUND;
import static com.modsen.rideservice.util.TestData.PASSENGER_SERVICE_NAME;
import static com.modsen.rideservice.util.TestData.PASSENGER_SERVICE_PORT;
import static com.modsen.rideservice.util.TestData.RIDE_ID;
import static com.modsen.rideservice.util.TestData.RIDE_SCRIPT;
import static com.modsen.rideservice.util.TestData.URL_RIDE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_ACCEPT;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_CANCEL;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_STATE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.getEmptyRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getEmptyRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getEmptyRideRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideRequestDto;
import static com.modsen.rideservice.util.TestData.getPageRideResponseDto;
import static com.modsen.rideservice.util.TestData.getRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDtoBuilder;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDto;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDtoBuilder;
import static com.modsen.rideservice.util.ViolationData.DESTINATION_ADDRESS_INVALID;
import static com.modsen.rideservice.util.ViolationData.DESTINATION_ADDRESS_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.DRIVER_ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.DRIVER_ID_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.rideservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.rideservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.rideservice.util.ViolationData.PASSENGER_ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.PASSENGER_ID_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.RIDE_STATE_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.SOURCE_ADDRESS_INVALID;
import static com.modsen.rideservice.util.ViolationData.SOURCE_ADDRESS_MANDATORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = RIDE_SCRIPT,
        executionPhase = BEFORE_TEST_METHOD
)
public class RideControllerIntegrationTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            Map<String, int[]> servicePortsMap = Map.of(
                    PASSENGER_SERVICE_NAME, new int[]{PASSENGER_SERVICE_PORT},
                    DRIVER_SERVICE_NAME, new int[]{DRIVER_SERVICE_PORT}
            );
            return new TestServiceInstanceListSupplier(servicePortsMap);
        }
    }

    @RegisterExtension
    static WireMockExtension PASSENGER_SERVICE = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(PASSENGER_SERVICE_PORT))
            .build();

    @RegisterExtension
    static WireMockExtension DRIVER_SERVICE = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(DRIVER_SERVICE_PORT))
            .build();

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private FeignClientStubs feignClientStubs;

    @BeforeAll
    static void init() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        feignClientStubs = new FeignClientStubs(objectMapper);
    }

    @Test
    void getPageRides_whenEmptyParams_thenReturns200() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_RIDE)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageRides_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_RIDE)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        PageDto<RideResponseDto> actualPageRideResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<RideResponseDto>>() {});
        assertEqualsForPageWithoutTimeAndCost(getPageRideResponseDto(), actualPageRideResponseDto);
    }

    @Test
    void getPageRides_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_RIDE)
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
    void getPageRides_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_RIDE)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRidesByDriverId_whenEmptyParams_thenReturns200() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_RIDE_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageRidesByDriverId_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_RIDE_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        PageDto<RideResponseDto> actualPageRideResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<RideResponseDto>>() {});
        assertEqualsForPageWithoutTimeAndCost(getPageRideResponseDto(), actualPageRideResponseDto);
    }

    @Test
    void getPageRidesByDriverId_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_RIDE_DRIVER_ID, DRIVER_ID.toString())
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
    void getPageRidesByDriverId_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_RIDE_DRIVER_ID, DRIVER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRidesByDriverId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_RIDE_DRIVER_ID, INVALID_DRIVER_ID)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRidesByPassengerId_whenEmptyParams_thenReturns200() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_RIDE_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageRidesByPassengerId_whenValidParams_thenReturns200AndResponseDto() throws JsonProcessingException {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_RIDE_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        PageDto<RideResponseDto> actualPageRideResponseDto = objectMapper.readValue(responseJson,
                new TypeReference<PageDto<RideResponseDto>>() {});
        assertEqualsForPageWithoutTimeAndCost(getPageRideResponseDto(), actualPageRideResponseDto);
    }

    @Test
    void getPageRidesByPassengerId_whenInsufficientParams_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_RIDE_PASSENGER_ID, PASSENGER_ID.toString())
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
    void getPageRidesByPassengerId_whenLimitExceeded_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_RIDE_PASSENGER_ID, PASSENGER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRidesByPassengerId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_RIDE_PASSENGER_ID, INVALID_PASSENGER_ID)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getRide_whenValidId_thenReturns200AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_RIDE_ID, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(getRideResponseDto(), actualRideResponseDto);
    }

    @Test
    void getRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_RIDE_ID, INSUFFICIENT_RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actual = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actual);
    }

    @Test
    void createRide_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        RideCreateRequestDto rideRequestDto = getRideCreateRequestDto();
        feignClientStubs.stubForPassengerServiceWithExistingPassenger(PASSENGER_ID, PASSENGER_SERVICE);
        feignClientStubs.stubForDriverServiceWithExistingDriver(DRIVER_ID, DRIVER_SERVICE);
        RideResponseDto expectedRideResponseDto = getRideResponseDtoBuilder()
                .id(2L)
                .driverId(null)
                .build();

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .post(URL_RIDE)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(expectedRideResponseDto, actualRideResponseDto);
    }

    @Test
    void createRide_whenPassengerDoesntExist_thenReturns404AndErrorResult() throws Exception {
        RideCreateRequestDto rideRequestDto = getRideCreateRequestDto();
        feignClientStubs.stubForPassengerServiceWithNonExistingPassenger(PASSENGER_ID, PASSENGER_SERVICE);
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), PASSENGER_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .post(URL_RIDE)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void createRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideCreateRequestDto rideRequestDto = getEmptyRideCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_MANDATORY, DESTINATION_ADDRESS_MANDATORY, SOURCE_ADDRESS_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .post(URL_RIDE)
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
    void createRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideCreateRequestDto rideRequestDto = getInvalidRideCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID, DESTINATION_ADDRESS_INVALID, SOURCE_ADDRESS_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .post(URL_RIDE)
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
    void acceptRide_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        feignClientStubs.stubForDriverServiceWithExistingDriver(DRIVER_ID, DRIVER_SERVICE);
        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        RideResponseDto expectedRideResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.ACCEPTED)
                .driverId(DRIVER_ID)
                .build();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(expectedRideResponseDto, actualRideResponseDto);
    }

    @Test
    void acceptRide_whenDriverDoesntExist_thenReturns404AndErrorResult() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        feignClientStubs.stubForDriverServiceWithNonExistingDriver(DRIVER_ID, DRIVER_SERVICE);
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), DRIVER_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void acceptRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getEmptyRideAcceptRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, RIDE_ID.toString())
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
    void acceptRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getInvalidRideAcceptRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, RIDE_ID.toString())
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
    void acceptRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideAcceptRequestDto)
                .when()
                .put(URL_RIDE_ID_ACCEPT, INSUFFICIENT_RIDE_ID.toString())
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
    void cancelRide_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .when()
                .put(URL_RIDE_ID_CANCEL, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        RideResponseDto expectedRideResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.CANCELLED)
                .build();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(expectedRideResponseDto, actualRideResponseDto);
    }

    @Test
    void cancelRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .when()
                .put(URL_RIDE_ID_CANCEL, INSUFFICIENT_RIDE_ID.toString())
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
    void updateRide_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        feignClientStubs.stubForPassengerServiceWithExistingPassenger(PASSENGER_ID, PASSENGER_SERVICE);
        feignClientStubs.stubForDriverServiceWithExistingDriver(DRIVER_ID, DRIVER_SERVICE);
        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(getRideResponseDto(), actualRideResponseDto);
    }

    @Test
    void updateRide_whenPassengerDoesntExist_thenReturns404AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        feignClientStubs.stubForPassengerServiceWithNonExistingPassenger(PASSENGER_ID, PASSENGER_SERVICE);
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), PASSENGER_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void updateRide_whenDriverDoesntExist_thenReturns404AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        feignClientStubs.stubForPassengerServiceWithExistingPassenger(PASSENGER_ID, PASSENGER_SERVICE);
        feignClientStubs.stubForDriverServiceWithNonExistingDriver(DRIVER_ID, DRIVER_SERVICE);
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), DRIVER_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void updateRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getEmptyRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_MANDATORY, SOURCE_ADDRESS_MANDATORY, DESTINATION_ADDRESS_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, RIDE_ID.toString())
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
    void updateRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getInvalidRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID, DRIVER_ID_INVALID, SOURCE_ADDRESS_INVALID, DESTINATION_ADDRESS_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, RIDE_ID.toString())
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
    void updateRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideRequestDto)
                .when()
                .put(URL_RIDE_ID, INSUFFICIENT_RIDE_ID.toString())
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
    void updateRideState_whenValidInput_thenReturns200AndResponseDto() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        RideResponseDto expectedRideResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.ACCEPTED)
                .build();

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStateRequestDto)
                .when()
                .put(URL_RIDE_ID_STATE, RIDE_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RideResponseDto actualRideResponseDto = objectMapper.readValue(responseJson, RideResponseDto.class);

        assertEqualsWithoutTimeAndCost(expectedRideResponseDto, actualRideResponseDto);
    }

    @Test
    void updateRideState_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDtoBuilder()
                .rideState(null)
                .build();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(HttpStatus.BAD_REQUEST.value(),
                List.of(RIDE_STATE_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStateRequestDto)
                .when()
                .put(URL_RIDE_ID_STATE, RIDE_ID.toString())
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

    private void assertEqualsWithoutTimeAndCost(RideResponseDto expected, RideResponseDto actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.driverId(), actual.driverId());
        assertEquals(expected.passengerId(), actual.passengerId());
        assertEquals(expected.sourceAddress(), actual.sourceAddress());
        assertEquals(expected.destinationAddress(), actual.destinationAddress());
        assertEquals(expected.rideState(), actual.rideState());
    }

    private void assertEqualsForPageWithoutTimeAndCost(PageDto<RideResponseDto> expectedPage, PageDto<RideResponseDto> actualPage) {
        assertEquals(expectedPage.pageNumber(), actualPage.pageNumber());
        assertEquals(expectedPage.pageSize(), actualPage.pageSize());
        assertEquals(expectedPage.totalPages(), actualPage.totalPages());
        assertEquals(expectedPage.totalElements(), actualPage.totalElements());
        for(int i = 0; i < expectedPage.content().size(); i++) {
            assertEqualsWithoutTimeAndCost(expectedPage.content().get(i), actualPage.content().get(i));
        }
    }
}
