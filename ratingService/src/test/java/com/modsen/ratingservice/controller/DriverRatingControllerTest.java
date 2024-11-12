package com.modsen.ratingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.exception.ListErrorMessage;
import com.modsen.ratingservice.service.impl.DriverRatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.modsen.ratingservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_USER_ID;
import static com.modsen.ratingservice.util.TestData.LIMIT;
import static com.modsen.ratingservice.util.TestData.LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.OFFSET;
import static com.modsen.ratingservice.util.TestData.OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.RATING_ID;
import static com.modsen.ratingservice.util.TestData.URL_DRIVER_RATING;
import static com.modsen.ratingservice.util.TestData.URL_DRIVER_RATING_USER_ID;
import static com.modsen.ratingservice.util.TestData.USER_ID;
import static com.modsen.ratingservice.util.TestData.getEmptyRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getInvalidRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDto;
import static com.modsen.ratingservice.util.ViolationData.limitBig;
import static com.modsen.ratingservice.util.ViolationData.limitInvalid;
import static com.modsen.ratingservice.util.ViolationData.offsetInvalid;
import static com.modsen.ratingservice.util.ViolationData.ratingInvalid;
import static com.modsen.ratingservice.util.ViolationData.ratingMandatory;
import static com.modsen.ratingservice.util.ViolationData.rideIdInvalid;
import static com.modsen.ratingservice.util.ViolationData.rideIdMandatory;
import static com.modsen.ratingservice.util.ViolationData.userIdInvalid;
import static com.modsen.ratingservice.util.ViolationData.userIdMandatory;
import static com.modsen.ratingservice.util.TestData.URL_DRIVER_RATING_ID;

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

@WebMvcTest(controllers = DriverRatingController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class DriverRatingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DriverRatingServiceImpl ratingService;

    @Test
    void getRating_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING_ID, RATING_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        RatingResponseDto ratingResponseDto = getRatingResponseDto();
        when(ratingService.getRatingById(RATING_ID)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING_ID, RATING_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void getPageRatings_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatings(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPageRatings_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatings_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING)
                        .param(OFFSET, INSUFFICIENT_OFFSET_VALUE.toString())
                        .param(LIMIT, INSUFFICIENT_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatings_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING)
                .param(OFFSET, EXCEEDED_OFFSET_VALUE.toString())
                .param(LIMIT, EXCEEDED_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatingsByUserId(USER_ID, OFFSET_VALUE, LIMIT_VALUE);
    }

    @Test
    void getPageRatingsByUserId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatingsByUserId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID)
                        .param(OFFSET, INSUFFICIENT_OFFSET_VALUE.toString())
                        .param(LIMIT, INSUFFICIENT_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID)
                        .param(OFFSET, EXCEEDED_OFFSET_VALUE.toString())
                        .param(LIMIT, EXCEEDED_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRatingsByUserId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(userIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, INSUFFICIENT_USER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRating_whenValidInput_thenReturns201() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);
        verify(ratingService, times(1)).addRating(ratingCaptor.capture());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void createRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        RatingResponseDto ratingResponseDto = getRatingResponseDto();
        when(ratingService.addRating(ratingRequestDto)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void createRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto emptyRatingRequestDto = getEmptyRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ratingMandatory,rideIdMandatory,userIdMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto invalidRatingRequestDto = getInvalidRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ratingInvalid, userIdInvalid, rideIdInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRating_whenValidInput_thenReturns200() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);

        verify(ratingService, times(1)).updateRating(anyString(), ratingCaptor.capture());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void updateRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        RatingResponseDto ratingResponseDto = getRatingResponseDto();
        when(ratingService.updateRating(RATING_ID, ratingRequestDto)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(ratingResponseDto));
    }

    @Test
    void updateRating_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto emptyRatingRequestDto = getEmptyRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ratingMandatory,rideIdMandatory,userIdMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRating_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RatingRequestDto invalidRatingRequestDto = getInvalidRatingRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ratingInvalid, userIdInvalid, rideIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deleteRating_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL_DRIVER_RATING_ID, RATING_ID))
                .andExpect(status().isNoContent());
    }
}