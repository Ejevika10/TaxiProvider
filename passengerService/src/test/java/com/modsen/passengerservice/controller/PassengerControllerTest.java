package com.modsen.passengerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.exception.ListErrorMessage;
import com.modsen.passengerservice.service.PassengerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.modsen.passengerservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.INSUFFICIENT_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.LIMIT;
import static com.modsen.passengerservice.util.TestData.LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.OFFSET;
import static com.modsen.passengerservice.util.TestData.OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER;
import static com.modsen.passengerservice.util.TestData.URL_PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.getEmptyPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getInvalidPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDto;
import static com.modsen.passengerservice.util.ViolationData.limitExceeded;
import static com.modsen.passengerservice.util.ViolationData.limitInsufficient;
import static com.modsen.passengerservice.util.ViolationData.offsetInsufficient;
import static com.modsen.passengerservice.util.ViolationData.passengerEmailInvalid;
import static com.modsen.passengerservice.util.ViolationData.passengerEmailMandatory;
import static com.modsen.passengerservice.util.ViolationData.passengerIdInvalid;
import static com.modsen.passengerservice.util.ViolationData.passengerNameInvalid;
import static com.modsen.passengerservice.util.ViolationData.passengerNameMandatory;
import static com.modsen.passengerservice.util.ViolationData.passengerPhoneInvalid;
import static com.modsen.passengerservice.util.ViolationData.passengerPhoneMandatory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerController.class)
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
                List.of(offsetInsufficient, limitInsufficient));

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
    void getPagePassengers_whenBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitExceeded));

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
    void getPassengerById_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID))
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
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();
        mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();
        mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<PassengerRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerRequestDto.class);
        verify(passengerService, times(1)).addPassenger(passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void createPassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerService.addPassenger(passengerRequestDto)).thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void createPassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto emptyPassengerRequestDto = getEmptyPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerPhoneMandatory, passengerNameMandatory, passengerEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPassengerRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createPassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto invalidPassengerRequestDto = getInvalidPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerPhoneInvalid, passengerNameInvalid, passengerEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPassengerRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenValidInput_thenReturns200() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();

        mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();

        mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<PassengerRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerRequestDto.class);

        verify(passengerService, times(1)).updatePassenger(anyLong(), passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void updatePassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();

        when(passengerService.updatePassenger(PASSENGER_ID, passengerRequestDto))
                .thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void updatePassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto emptyPassengerRequestDto = getEmptyPassengerRequestDto();

        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerPhoneMandatory, passengerNameMandatory, passengerEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPassengerRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto invalidPassengerRequestDto = getInvalidPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerPhoneInvalid, passengerNameInvalid, passengerEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPassengerRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updatePassenger_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        PassengerRequestDto passengerRequestDto = getPassengerRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deletePassenger_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL_PASSENGER_ID, PASSENGER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePassenger_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(delete(URL_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}