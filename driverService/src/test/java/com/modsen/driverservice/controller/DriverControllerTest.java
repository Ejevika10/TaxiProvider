package com.modsen.driverservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.exception.ListErrorMessage;
import com.modsen.driverservice.service.DriverService;
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
import static com.modsen.driverservice.util.TestData.URL_DRIVER;
import static com.modsen.driverservice.util.TestData.URL_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.getDriverRequestDto;
import static com.modsen.driverservice.util.TestData.getDriverResponseDto;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DriverController.class)
class DriverControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DriverService driverService;

    @Test
    void getPageDrivers_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER))
                .andExpect(status().isOk());
        verify(driverService, times(1)).getPageDrivers(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPageDrivers_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageDrivers_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER)
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
    void getPageDrivers_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER)
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
    void getDriverById_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getDriverById_whenValidInput_thenReturnsResponseDto() throws Exception {
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverService.getDriverById(DRIVER_ID)).thenReturn(driverResponseDto);

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void getDriverById_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(get(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID))
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
    void createDriver_whenValidInput_thenReturns201() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();

        mockMvc.perform(post(URL_DRIVER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createDriver_whenValidInput_thenMapsToBusinessModel() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();

        mockMvc.perform(post(URL_DRIVER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated());

        ArgumentCaptor<DriverRequestDto> driverCaptor = ArgumentCaptor.forClass(DriverRequestDto.class);
        verify(driverService, times(1)).createDriver(driverCaptor.capture());
        assertThat(driverCaptor.getValue()).isEqualTo(driverRequestDto);
    }

    @Test
    void createDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverService.createDriver(driverRequestDto)).thenReturn(driverResponseDto);

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated())
            .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void createDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto emptyDriverRequestDto = getEmptyDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_MANDATORY, DRIVER_NAME_MANDATORY, DRIVER_EMAIL_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyDriverRequestDto)))
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
    void createDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto invalidDriverRequestDto = getInvalidDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_INVALID, DRIVER_NAME_INVALID, DRIVER_EMAIL_INVALID));

        MvcResult mvcResult = mockMvc.perform(post(URL_DRIVER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDriverRequestDto)))
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
    void updateDriver_whenValidInput_thenReturns200() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();

        mockMvc.perform(put(URL_DRIVER_ID, DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDriver_whenValidInput_thenMapsToBusinessModel() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();

        mockMvc.perform(put(URL_DRIVER_ID, DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<DriverRequestDto> driverCaptor = ArgumentCaptor.forClass(DriverRequestDto.class);
        verify(driverService, times(1)).updateDriver(anyLong(), driverCaptor.capture());
        assertThat(driverCaptor.getValue()).isEqualTo(driverRequestDto);
    }

    @Test
    void updateDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverService.updateDriver(DRIVER_ID, driverRequestDto)).thenReturn(driverResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_ID, DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void updateDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto emptyDriverRequestDto = getEmptyDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_MANDATORY, DRIVER_NAME_MANDATORY, DRIVER_EMAIL_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_ID, DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDriverRequestDto)))
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
    void updateDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto invalidDriverRequestDto = getInvalidDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_PHONE_INVALID, DRIVER_NAME_INVALID, DRIVER_EMAIL_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_ID, DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDriverRequestDto)))
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
    void updateDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        DriverRequestDto driverRequestDto = getDriverRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
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
    void deleteDriver_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(delete(URL_DRIVER_ID, INSUFFICIENT_DRIVER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode())
                .isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages())
                .containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}