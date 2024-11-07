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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

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
    private final PassengerRequestDto passengerRequestDto = new PassengerRequestDto("PassengerA", "PassengerA@email.com", "71234567890", 0.0);
    private final PassengerRequestDto emptyPassengerRequestDto = new PassengerRequestDto(null, null, null, null);
    private final PassengerRequestDto invalidPassengerRequestDto = new PassengerRequestDto("A", "DriverA", "11", -1.0);
    private final PassengerResponseDto passengerResponseDto = new PassengerResponseDto(1L, "PassengerA", "PassengerA@email.com", "71234567890", 0.0);

    private final Long passengerId = 1L;
    private final Long invalidPassengerId = -1L;
    private final String URL = "/api/v1/passengers";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PassengerService passengerService;

    private final String passengerNameMandatory = "name: Name is mandatory";
    private final String passengerEmailMandatory = "email: Email is mandatory";
    private final String passengerPhoneMandatory = "phone: Phone is mandatory";
    private final String passengerNameInvalid = "name: size must be between 2 and 50";
    private final String passengerEmailInvalid = "email: Email is invalid";
    private final String passengerPhoneInvalid = "phone: Phone is invalid";
    private final String passengerIdInvalid = "id: must be greater than or equal to 0";
    private final String offsetInvalid = "offset: must be greater than or equal to 0";
    private final String limitInvalid = "limit: must be greater than or equal to 1";
    private final String limitBig = "limit: must be less than or equal to 20";


    @Test
    void getPagePassengers_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(passengerService, times(1)).getPagePassengers(0,5);
    }

    @Test
    void getPagePassengers_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL).param("offset", "0").param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPagePassengers_withInvalidParams_thenReturns400() throws Exception {
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
    void getPagePassengers_withBigLimit_thenReturns400() throws Exception {
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
    void getPassengerById_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/{passengerId}", passengerId))
                .andExpect(status().isOk());
    }

    @Test
    void getPassengerById_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(passengerService.getPassengerById(passengerId)).thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL + "/{passengerId}", passengerId))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void getPassengerById_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{passengerId}", invalidPassengerId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createPassenger_whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<PassengerRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerRequestDto.class);
        verify(passengerService, times(1)).addPassenger(passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void createPassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(passengerService.addPassenger(passengerRequestDto)).thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void createPassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerPhoneMandatory, passengerNameMandatory, passengerEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL)
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
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerPhoneInvalid, passengerNameInvalid, passengerEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL)
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
        mockMvc.perform(put(URL + "/{passengerId}", passengerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassenger_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{passengerId}", passengerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<PassengerRequestDto> passengerCaptor = ArgumentCaptor.forClass(PassengerRequestDto.class);

        verify(passengerService, times(1)).updatePassenger(anyLong(), passengerCaptor.capture());
        assertThat(passengerCaptor.getValue()).isEqualTo(passengerRequestDto);
    }

    @Test
    void updatePassenger_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(passengerService.updatePassenger(1L, passengerRequestDto))
                .thenReturn(passengerResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{passengerId}", passengerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(passengerResponseDto));
    }

    @Test
    void updatePassenger_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerPhoneMandatory, passengerNameMandatory, passengerEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{passengerId}", passengerId)
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
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerPhoneInvalid, passengerNameInvalid, passengerEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{passengerId}", passengerId)
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
    void updatePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{passengerId}", invalidPassengerId)
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
        mockMvc.perform(delete(URL + "/{passengerId}", passengerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePassenger_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(delete(URL + "/{passengerId}", invalidPassengerId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}