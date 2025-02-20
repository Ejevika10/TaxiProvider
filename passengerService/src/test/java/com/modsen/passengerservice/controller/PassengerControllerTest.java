package com.modsen.passengerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.GlobalExceptionHandler;
import com.modsen.exceptionstarter.message.ListErrorMessage;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.service.PassengerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static com.modsen.passengerservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INVALID_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.LIMIT;
import static com.modsen.passengerservice.util.TestData.LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.OFFSET;
import static com.modsen.passengerservice.util.TestData.OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerController.class)
@WithMockUser
@Import(GlobalExceptionHandler.class)
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PassengerService passengerService;

    @Test
    void getPagePassengers_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_PASSENGER))
                .andExpect(status().isOk());
        verify(passengerService, times(1)).getPagePassengers(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPagePassengers_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_PASSENGER)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPagePassengers_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        MvcResult mvcResult = mockMvc.perform(get(URL_PASSENGER)
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
    void getPagePassengers_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        MvcResult mvcResult = mockMvc.perform(get(URL_PASSENGER)
                        .param(OFFSET, EXCEEDED_OFFSET_VALUE.toString())
                        .param(LIMIT, EXCEEDED_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPassengerById_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_PASSENGER_ID, PASSENGER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getPassengerById_whenValidInput_thenReturnsResponseDto() throws Exception {
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerService.getPassengerById(PASSENGER_ID)).thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL_PASSENGER_ID, PASSENGER_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void getPassengerById_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        MvcResult mvcResult = mockMvc.perform(get(URL_PASSENGER_ID, INVALID_PASSENGER_ID))
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
    void createPassenger_whenValidInput_thenReturns201() throws Exception {
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDto();
        mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void createPassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDto();
        mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated());

        ArgumentCaptor<PassengerCreateRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerCreateRequestDto.class);
        verify(passengerService, times(1)).addPassenger(passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void createPassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerService.addPassenger(passengerRequestDto)).thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void createPassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerCreateRequestDto emptyPassengerRequestDto = getEmptyPassengerCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_MANDATORY, PASSENGER_PHONE_MANDATORY, PASSENGER_NAME_MANDATORY, PASSENGER_EMAIL_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPassengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createPassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerCreateRequestDto invalidPassengerRequestDto = getInvalidPassengerCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_INVALID, PASSENGER_NAME_INVALID, PASSENGER_EMAIL_INVALID));

        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPassengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenValidInput_thenReturns200() throws Exception {
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();

        mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();

        mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk());

        ArgumentCaptor<PassengerUpdateRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerUpdateRequestDto.class);

        verify(passengerService, times(1)).updatePassenger(any(UUID.class), passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void updatePassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();

        when(passengerService.updatePassenger(PASSENGER_ID, passengerRequestDto))
                .thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void updatePassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerUpdateRequestDto emptyPassengerRequestDto = getEmptyPassengerUpdateRequestDto();

        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_MANDATORY, PASSENGER_NAME_MANDATORY, PASSENGER_EMAIL_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPassengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerUpdateRequestDto invalidPassengerRequestDto = getInvalidPassengerUpdateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_PHONE_INVALID, PASSENGER_NAME_INVALID, PASSENGER_EMAIL_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPassengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, INVALID_PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deletePassenger_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL_PASSENGER_ID, PASSENGER_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(UUID_INVALID));

        MvcResult mvcResult = mockMvc.perform(delete(URL_PASSENGER_ID, INVALID_PASSENGER_ID)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}