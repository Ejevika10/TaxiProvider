package com.modsen.rideservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.exception.ListErrorMessage;
import com.modsen.rideservice.model.RideState;
import com.modsen.rideservice.service.RideService;
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

import static com.modsen.rideservice.util.TestData.DRIVER_ID;
import static com.modsen.rideservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_RIDE_ID;
import static com.modsen.rideservice.util.TestData.LIMIT;
import static com.modsen.rideservice.util.TestData.LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.OFFSET;
import static com.modsen.rideservice.util.TestData.OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.RIDE_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_STATE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.getEmptyRideRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDtoBuilder;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDto;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDtoBuilder;
import static com.modsen.rideservice.util.ViolationData.destinationAddressInvalid;
import static com.modsen.rideservice.util.ViolationData.destinationAddressMandatory;
import static com.modsen.rideservice.util.ViolationData.driverIdInvalid;
import static com.modsen.rideservice.util.ViolationData.idInvalid;
import static com.modsen.rideservice.util.ViolationData.limitBig;
import static com.modsen.rideservice.util.ViolationData.limitInvalid;
import static com.modsen.rideservice.util.ViolationData.offsetInvalid;
import static com.modsen.rideservice.util.ViolationData.passengerIdInvalid;
import static com.modsen.rideservice.util.ViolationData.passengerIdMandatory;
import static com.modsen.rideservice.util.ViolationData.rideStateMandatory;
import static com.modsen.rideservice.util.ViolationData.sourceAddressInvalid;
import static com.modsen.rideservice.util.ViolationData.sourceAddressMandatory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RideController.class)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    @Test
    void getPageRides_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRides(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPageRides_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRides_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE)
                        .param(OFFSET, INSUFFICIENT_OFFSET_VALUE.toString())
                        .param(LIMIT, INSUFFICIENT_LIMIT_VALUE.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRides_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE)
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
    void getPageRidesByDriverId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByDriverId(DRIVER_ID, OFFSET_VALUE, LIMIT_VALUE);

    }

    @Test
    void getPageRidesByDriverId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByDriverId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID)
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
    void getPageRidesByDriverId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID)
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
    void getPageRidesByDriverId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_DRIVER_ID, INSUFFICIENT_DRIVER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRidesByPassengerId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByPassengerId(PASSENGER_ID, OFFSET_VALUE,LIMIT_VALUE);

    }

    @Test
    void getPageRidesByPassengerId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByPassengerId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID)
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
    void getPageRidesByPassengerId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID)
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
    void getPageRidesByPassengerId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_PASSENGER_ID, INSUFFICIENT_PASSENGER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getRide_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_ID, RIDE_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideService.getRideById(RIDE_ID)).thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_ID, RIDE_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void getRide_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_ID, INSUFFICIENT_RIDE_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRide_whenValidInput_thenReturns201() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<RideRequestDto> rideCaptor = ArgumentCaptor.forClass(RideRequestDto.class);
        verify(rideService, times(1)).createRide(rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void createRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideResponseDto rideResponseDto = getRideResponseDto();
        RideRequestDto rideRequestDto = getRideRequestDto();
        when(rideService.createRide(rideRequestDto)).thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void createRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto emptyRideRequestDto = getEmptyRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdMandatory, destinationAddressMandatory, sourceAddressMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto invalidRideRequestDto = getInvalidRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid, driverIdInvalid, destinationAddressInvalid, sourceAddressInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRideRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRide_whenValidInput_thenReturns200() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RideRequestDto> rideCaptor = ArgumentCaptor.forClass(RideRequestDto.class);

        verify(rideService, times(1)).updateRide(anyLong(), rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void updateRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideService.updateRide(RIDE_ID, rideRequestDto))
                .thenReturn(rideResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void updateRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto emptyRideRequestDto = getEmptyRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdMandatory, sourceAddressMandatory, destinationAddressMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideRequestDto)))
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
    void updateRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto invalidRideRequestDto = getInvalidRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(passengerIdInvalid, driverIdInvalid, sourceAddressInvalid, destinationAddressInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRideRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRide_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, INSUFFICIENT_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRideState_whenValidInput_thenReturns200() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRideState_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RideStateRequestDto> rideCaptor = ArgumentCaptor.forClass(RideStateRequestDto.class);

        verify(rideService, times(1)).setNewState(anyLong(), rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideStateRequestDto);
    }

    @Test
    void updateRideState_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        RideResponseDto rideNewStateResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.ACCEPTED)
                .build();
        when(rideService.setNewState(RIDE_ID, rideStateRequestDto))
                .thenReturn(rideNewStateResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideNewStateResponseDto));
    }

    @Test
    void updateRideState_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideStateRequestDto emptyRideStateRequestDto = getRideStateRequestDtoBuilder()
                .rideState(null)
                .build();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(HttpStatus.BAD_REQUEST.value(),
                List.of(rideStateMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideStateRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}