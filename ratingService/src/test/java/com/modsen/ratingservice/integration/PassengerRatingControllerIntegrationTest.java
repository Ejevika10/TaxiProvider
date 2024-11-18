package com.modsen.ratingservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jayway.jsonpath.JsonPath;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.exception.ErrorMessage;
import com.modsen.ratingservice.exception.ListErrorMessage;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.util.TestServiceInstanceListSupplier;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.modsen.ratingservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_USER_ID;
import static com.modsen.ratingservice.util.TestData.INVALID_USER_ID;
import static com.modsen.ratingservice.util.TestData.LIMIT;
import static com.modsen.ratingservice.util.TestData.LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.OFFSET;
import static com.modsen.ratingservice.util.TestData.OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.PAGE_NUMBER;
import static com.modsen.ratingservice.util.TestData.PAGE_SIZE;
import static com.modsen.ratingservice.util.TestData.PASSENGER_ID_INVALID;
import static com.modsen.ratingservice.util.TestData.RATING_ID;
import static com.modsen.ratingservice.util.TestData.RIDE_ID;
import static com.modsen.ratingservice.util.TestData.RIDE_NOT_FOUND;
import static com.modsen.ratingservice.util.TestData.RIDE_SERVICE_NAME;
import static com.modsen.ratingservice.util.TestData.RIDE_SERVICE_PORT;
import static com.modsen.ratingservice.util.TestData.UNIQUE_RIDE_ID;
import static com.modsen.ratingservice.util.TestData.URL_PASSENGER_RATING;
import static com.modsen.ratingservice.util.TestData.URL_PASSENGER_RATING_ID;
import static com.modsen.ratingservice.util.TestData.URL_PASSENGER_RATING_USER_ID;
import static com.modsen.ratingservice.util.TestData.URL_RIDES;
import static com.modsen.ratingservice.util.TestData.USER_ID;
import static com.modsen.ratingservice.util.TestData.getEmptyRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getInvalidRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getPassengerRating;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDtoBuilder;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDto;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoBuilder;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoList;
import static com.modsen.ratingservice.util.TestData.getRideResponseDto;
import static com.modsen.ratingservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.ratingservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.ratingservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.ratingservice.util.ViolationData.RATING_INVALID;
import static com.modsen.ratingservice.util.ViolationData.RATING_MANDATORY;
import static com.modsen.ratingservice.util.ViolationData.RIDE_ID_INVALID;
import static com.modsen.ratingservice.util.ViolationData.RIDE_ID_MANDATORY;
import static com.modsen.ratingservice.util.ViolationData.USER_ID_INVALID;
import static com.modsen.ratingservice.util.ViolationData.USER_ID_MANDATORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerRatingControllerIntegrationTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            Map<String, int[]> servicePortsMap = Map.of(
                    RIDE_SERVICE_NAME, new int[]{RIDE_SERVICE_PORT}
            );
            return new TestServiceInstanceListSupplier(servicePortsMap);
        }
    }

    @RegisterExtension
    static WireMockExtension RIDE_SERVICE = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(RIDE_SERVICE_PORT))
            .build();

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:management");

    @DynamicPropertySource
    static void mongoDBProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    private PassengerRatingRepository ratingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        mongoDBContainer.start();
        rabbitMQContainer.start();
    }

    @BeforeEach
    void setUp(){
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        ratingRepository.deleteAll();
        ratingRepository.save(getPassengerRating());
        RIDE_SERVICE.resetRequests();
    }

    @Test
    void getRating_whenValidId_thenReturns201AndResponseDto() throws Exception {
        String responseJson = RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_RATING_ID, RATING_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RatingResponseDto actualRatingResponseDto = objectMapper.readValue(responseJson, RatingResponseDto.class);

        assertEquals(getRatingResponseDto(), actualRatingResponseDto);
    }

    @Test
    void getPageRatings_whenEmptyParams_thenReturns201() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageRatings_whenValidParams_thenReturns201() {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        List<Map<String, Object>> content = JsonPath.parse(responseJson).read("$.content");
        List<RatingResponseDto> actualRatingResponseDtoList = objectMapper.convertValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RatingResponseDto.class));
        assertEquals(getRatingResponseDtoList(), actualRatingResponseDtoList);
    }

    @Test
    void getPageRatings_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING)
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
    void getPageRatings_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRatingsByUserId_whenEmptyParams_thenReturns201() {
        RestAssuredMockMvc
                .given()
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, USER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(PAGE_NUMBER, equalTo(OFFSET_VALUE))
                .body(PAGE_SIZE, equalTo(LIMIT_VALUE));
    }

    @Test
    void getPageRatingsByUserId_whenValidParams_thenReturns201() {
        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, USER_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();

        List<Map<String, Object>> content = JsonPath.parse(responseJson).read("$.content");
        List<RatingResponseDto> actualRatingResponseDtoList = objectMapper.convertValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RatingResponseDto.class));
        assertEquals(getRatingResponseDtoList(), actualRatingResponseDtoList);
    }

    @Test
    void getPageRatingsByUserId_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, INSUFFICIENT_OFFSET_VALUE)
                .param(LIMIT, INSUFFICIENT_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, USER_ID.toString())
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
    void getPageRatingsByUserId_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, EXCEEDED_OFFSET_VALUE)
                .param(LIMIT, EXCEEDED_LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, USER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void getPageRatingsByUserId_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(USER_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .param(OFFSET, OFFSET_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL_PASSENGER_RATING_USER_ID, INSUFFICIENT_USER_ID.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ListErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void createRating_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        getRideById_whenRideExists(UNIQUE_RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        RatingResponseDto expectedRatingResponseDto = getRatingResponseDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RatingResponseDto actualRatingResponseDto = objectMapper.readValue(responseJson, RatingResponseDto.class);

        assertEqualsWithoutId(expectedRatingResponseDto, actualRatingResponseDto);
    }

    @Test
    void createRating_whenRideDoesntExist_thenReturns404() throws Exception {
        getRideById_whenRideNotExists(UNIQUE_RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), RIDE_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void createRating_whenUserIdInvalid_thenReturns409() throws Exception {
        getRideById_whenRideExists(UNIQUE_RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .userId(INVALID_USER_ID)
                .build();
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(), PASSENGER_ID_INVALID );

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void createRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto ratingRequestDto = getEmptyRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(RATING_MANDATORY, RIDE_ID_MANDATORY, USER_ID_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING)
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
    void createRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto ratingRequestDto = getInvalidRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(RATING_INVALID, USER_ID_INVALID, RIDE_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .post(URL_PASSENGER_RATING)
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
    void updateRating_whenValidInput_thenReturns201AndResponseDto() throws Exception {
        getRideById_whenRideExists(UNIQUE_RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        RatingResponseDto expectedRatingResponseDto = getRatingResponseDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, RATING_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .asString();
        RatingResponseDto actualRatingResponseDto = objectMapper.readValue(responseJson, RatingResponseDto.class);

        assertEquals(expectedRatingResponseDto, actualRatingResponseDto);
    }

    @Test
    void updateRating_whenRideDoesntExist_thenReturns404() throws Exception {
        getRideById_whenRideNotExists(UNIQUE_RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .rideId(UNIQUE_RIDE_ID)
                .build();
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(), RIDE_NOT_FOUND);

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, RATING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void updateRating_whenUserIdInvalid_thenReturns409() throws Exception {
        getRideById_whenRideExists(RIDE_ID);
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .userId(INVALID_USER_ID)
                .build();
        ErrorMessage expectedErrorResponse = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(), PASSENGER_ID_INVALID);

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, RATING_ID)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .asString();
        ErrorMessage actualErrorResponse = objectMapper.readValue(responseJson, ErrorMessage.class);

        assertEquals(expectedErrorResponse, actualErrorResponse);
    }

    @Test
    void updateRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto ratingRequestDto = getEmptyRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(RATING_MANDATORY, RIDE_ID_MANDATORY, USER_ID_MANDATORY));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, RATING_ID)
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
    void updateRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto ratingRequestDto = getInvalidRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(RATING_INVALID, USER_ID_INVALID, RIDE_ID_INVALID));

        String responseJson = RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ratingRequestDto)
                .when()
                .put(URL_PASSENGER_RATING_ID, RATING_ID)
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
    void deleteRating_whenValidId_thenReturns204() {
        System.out.println(ratingRepository.existsByIdAndDeletedIsFalse(RATING_ID));
        RestAssuredMockMvc
                .when()
                .delete(URL_PASSENGER_RATING_ID, RATING_ID)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void getRideById_whenRideExists(Long rideId) throws Exception {
        RIDE_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_RIDES + rideId.toString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(getRideResponseDto()))
                ));
    }

    private void getRideById_whenRideNotExists(Long rideId) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), RIDE_NOT_FOUND);
        RIDE_SERVICE.stubFor(WireMock.get(urlPathEqualTo(URL_RIDES + rideId.toString()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(errorMessage))
                ));
    }

    private void assertEqualsWithoutId(RatingResponseDto expected, RatingResponseDto actual) {
        assertEquals(expected.rating(), actual.rating());
        assertEquals(expected.comment(), actual.comment());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.rideId(), actual.rideId());
    }
}
