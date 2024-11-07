package com.modsen.ratingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.exception.ListErrorMessage;
import com.modsen.ratingservice.service.impl.PassengerRatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerRatingController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class PassengerRatingControllerTest {

    private final RatingRequestDto ratingRequestDto = new RatingRequestDto(1L, 1L, 5, "awesome");
    private final RatingRequestDto emptyRatingRequestDto = new RatingRequestDto(null, null, null, null);
    private final RatingRequestDto invalidRatingRequestDto = new RatingRequestDto(-1L, -1L, 7, null);
    private final RatingResponseDto ratingResponseDto = new RatingResponseDto("qwertyuiop1234", 1L, 1L, 5, "awesome");

    private final String id = "qwertyuiop1234";

    private final Long userId = 1L;
    private final Long invalidUserId = -1L;
    private final String URL = "/api/v1/passengerratings";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PassengerRatingServiceImpl ratingService;

    private final String userIdMandatory = "userId: User id is mandatory";
    private final String rideIdMandatory = "rideId: Ride id is mandatory";
    private final String ratingMandatory = "rating: Rating is mandatory";

    private final String userIdInvalid = "userId: must be greater than or equal to 0";
    private final String rideIdInvalid = "rideId: must be greater than or equal to 0";
    private final String ratingInvalid = "rating: must be less than or equal to 5";

    private final String offsetInvalid = "offset: must be greater than or equal to 0";
    private final String limitInvalid = "limit: must be greater than or equal to 1";
    private final String limitBig = "limit: must be less than or equal to 20";

    @Test
    void getRating_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/{ratingId}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(ratingService.getRatingById(id)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL + "/{ratingId}", id))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void getPageRatings_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatings(0,5);
    }

    @Test
    void getPageRatings_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL).param("offset", "0").param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatings_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL).param("offset", "-1").param("limit", "-1"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatings_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL).param("offset", "100").param("limit", "100"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/user/{userId}", userId))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatingsByUserId(userId, 0,5);
    }

    @Test
    void getPageRatingsByUserId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/user/{userId}", userId)
                        .param("offset", "0")
                        .param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatingsByUserId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/user/{userId}", userId)
                        .param("offset", "-1")
                        .param("limit", "-1"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/user/{userId}", userId)
                        .param("offset", "100")
                        .param("limit", "100"))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(userIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/user/{userId}", invalidUserId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRating_whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);
        verify(ratingService, times(1)).addRating(ratingCaptor.capture());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void createRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(ratingService.addRating(ratingRequestDto)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void createRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(ratingMandatory,rideIdMandatory,userIdMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(ratingInvalid, userIdInvalid, rideIdInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRating_whenValidInput_thenReturns200() throws Exception {
        mockMvc.perform(put(URL + "/{ratingId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{ratingId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);

        verify(ratingService, times(1)).updateRating(anyString(), ratingCaptor.capture());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void updateRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(ratingService.updateRating(id, ratingRequestDto)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{ratingId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void updateRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(ratingMandatory,rideIdMandatory,userIdMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{ratingId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(ratingInvalid, userIdInvalid, rideIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{ratingId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deleteRating_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL + "/{ratingId}", id))
                .andExpect(status().isNoContent());
    }
}