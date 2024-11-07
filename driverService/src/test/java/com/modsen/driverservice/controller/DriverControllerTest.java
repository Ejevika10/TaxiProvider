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

@WebMvcTest(controllers = DriverController.class)
class DriverControllerTest {
    private final DriverRequestDto driverRequestDto = new DriverRequestDto("DriverA", "DriverA@email.com", 0.0, "71234567890");
    private final DriverRequestDto emptyDriverRequestDto = new DriverRequestDto(null, null, null, null);
    private final DriverRequestDto invalidDriverRequestDto = new DriverRequestDto("A", "DriverA", null, "11");
    private final DriverResponseDto driverResponseDto = new DriverResponseDto(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0);

    private final Long driverId = 1L;
    private final Long invalidDriverId = -1L;
    private final String URL = "/api/v1/drivers";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DriverService driverService;
    private final String driverNameMandatory = "name: Name is mandatory";
    private final String driverEmailMandatory = "email: Email is mandatory";
    private final String driverPhoneMandatory = "phone: Phone is mandatory";
    private final String driverNameInvalid = "name: size must be between 4 and 100";
    private final String driverEmailInvalid = "email: Email is invalid";
    private final String driverPhoneInvalid = "phone: Phone is invalid";
    private final String driverIdInvalid = "id: must be greater than or equal to 0";
    private final String offsetInvalid = "offset: must be greater than or equal to 0";
    private final String limitInvalid = "limit: must be greater than or equal to 1";
    private final String limitBig = "limit: must be less than or equal to 20";

    @Test
    void getPageDrivers_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(driverService, times(1)).getPageDrivers(0,5);
    }

    @Test
    void getPageDrivers_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL).param("offset", "0").param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageDrivers_withInvalidParams_thenReturns400() throws Exception {
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
    void getPageDrivers_withBigLimit_thenReturns400() throws Exception {
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
    void getDriverById_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/{driverId}", driverId))
                .andExpect(status().isOk());
    }

    @Test
    void getDriverById_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(driverService.getDriverById(driverId)).thenReturn(driverResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL + "/{driverId}", driverId))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void getDriverById_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{driverId}", invalidDriverId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createDriver_whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createDriver_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated());

        ArgumentCaptor<DriverRequestDto> driverCaptor = ArgumentCaptor.forClass(DriverRequestDto.class);
        verify(driverService, times(1)).createDriver(driverCaptor.capture());
        assertThat(driverCaptor.getValue()).isEqualTo(driverRequestDto);
    }

    @Test
    void createDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(driverService.createDriver(driverRequestDto)).thenReturn(driverResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRequestDto)))
            .andExpect(status().isCreated())
            .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void createDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400, List.of(driverPhoneMandatory, driverNameMandatory, driverEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyDriverRequestDto)))
            .andExpect(status().isBadRequest())
            .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400, List.of(driverPhoneInvalid, driverNameInvalid, driverEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDriverRequestDto)))
            .andExpect(status().isBadRequest())
            .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateDriver_whenValidInput_thenReturns200() throws Exception {
        mockMvc.perform(put(URL + "/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDriver_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<DriverRequestDto> driverCaptor = ArgumentCaptor.forClass(DriverRequestDto.class);

        verify(driverService, times(1)).updateDriver(anyLong(), driverCaptor.capture());
        assertThat(driverCaptor.getValue()).isEqualTo(driverRequestDto);
    }

    @Test
    void updateDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(driverService.updateDriver(1L, driverRequestDto)).thenReturn(driverResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(driverResponseDto));
    }

    @Test
    void updateDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400, List.of(driverPhoneMandatory, driverNameMandatory, driverEmailMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDriverRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400, List.of(driverPhoneInvalid, driverNameInvalid, driverEmailInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDriverRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateDriver_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{driverId}", invalidDriverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deleteDriver_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL + "/{driverId}", driverId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDriver_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(delete(URL + "/{driverId}", invalidDriverId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}