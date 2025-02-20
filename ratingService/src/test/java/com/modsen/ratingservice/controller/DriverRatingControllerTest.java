package com.modsen.ratingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.GlobalExceptionHandler;
import com.modsen.exceptionstarter.message.ListErrorMessage;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.service.impl.DriverRatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.modsen.ratingservice.util.TestData.AUTHORIZATION;
import static com.modsen.ratingservice.util.TestData.AUTHORIZATION_VALUE;
import static com.modsen.ratingservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.INVALID_USER_ID;
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
import static com.modsen.ratingservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.ratingservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.ratingservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.ratingservice.util.ViolationData.RATING_INVALID;
import static com.modsen.ratingservice.util.ViolationData.RATING_MANDATORY;
import static com.modsen.ratingservice.util.ViolationData.RIDE_ID_INVALID;
import static com.modsen.ratingservice.util.ViolationData.RIDE_ID_MANDATORY;
import static com.modsen.ratingservice.util.ViolationData.USER_ID_INVALID;
import static com.modsen.ratingservice.util.ViolationData.USER_ID_MANDATORY;
import static com.modsen.ratingservice.util.TestData.URL_DRIVER_RATING_ID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DriverRatingController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
@WithMockUser
@Import(GlobalExceptionHandler.class)
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
    void getPageRatings_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatings(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPageRatings_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatings_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

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
    void getPageRatings_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

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
    void getPageRatingsByUserId_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID))
                .andExpect(status().isOk());
        verify(ratingService, times(1)).getPageRatingsByUserId(USER_ID, OFFSET_VALUE, LIMIT_VALUE);
    }

    @Test
    void getPageRatingsByUserId_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, USER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRatingsByUserId_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

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
    void getPageRatingsByUserId_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

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
                List.of(USER_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_RATING_USER_ID, INVALID_USER_ID))
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
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void createRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);
        verify(ratingService, times(1)).addRating(ratingCaptor.capture(), anyString());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void createRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        RatingResponseDto ratingResponseDto = getRatingResponseDto();
        when(ratingService.addRating(ratingRequestDto, AUTHORIZATION_VALUE)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(RATING_MANDATORY, RIDE_ID_MANDATORY, USER_ID_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(RATING_INVALID, USER_ID_INVALID, RIDE_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER_RATING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void updateRating_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());

        ArgumentCaptor<RatingRequestDto> ratingCaptor = ArgumentCaptor.forClass(RatingRequestDto.class);

        verify(ratingService, times(1)).updateRating(anyString(), ratingCaptor.capture(), anyString());
        assertThat(ratingCaptor.getValue()).isEqualTo(ratingRequestDto);
    }

    @Test
    void updateRating_whenValidInput_thenReturnsResponseDto() throws Exception {
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        RatingResponseDto ratingResponseDto = getRatingResponseDto();
        when(ratingService.updateRating(RATING_ID, ratingRequestDto, AUTHORIZATION_VALUE)).thenReturn(ratingResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(RATING_MANDATORY, RIDE_ID_MANDATORY, USER_ID_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRatingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(RATING_INVALID, USER_ID_INVALID, RIDE_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_RATING_ID, RATING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
        mockMvc.perform(delete(URL_DRIVER_RATING_ID, RATING_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}