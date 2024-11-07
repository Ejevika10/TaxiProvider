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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

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
    private final RideRequestDto rideRequestDto = new RideRequestDto(1L, 1L, "Source address", "Destination address", RideState.CREATED, LocalDateTime.now(), 1000);
    private final RideRequestDto emptyRideRequestDto = new RideRequestDto(null, null, null, null, null, null, null);
    private final RideRequestDto invalidRideRequestDto = new RideRequestDto(-1L, -1L, "addr", "addr", null, null, -1000);
    private final RideResponseDto rideResponseDto = new RideResponseDto(1L, 1L, 1L, "Source address", "Destination address", RideState.CREATED, LocalDateTime.now(), 1000);

    private final RideStateRequestDto rideStateRequestDto = new RideStateRequestDto("accepted");
    private final RideStateRequestDto emptyRideStateRequestDto = new RideStateRequestDto(null);
    private final RideResponseDto rideNewStateResponseDto = new RideResponseDto(1L, 1L, 1L, "Source address", "Destination address", RideState.ACCEPTED, LocalDateTime.now(), 1000);

    private final Long rideId = 1L;
    private final Long invalidRideId = -1L;
    private final Long driverId = 1L;
    private final Long invalidDriverId = -1L;
    private final Long passengerId = 1L;
    private final Long invalidPassengerId = -1L;

    private final String URL = "/api/v1/rides";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    private final String sourceAddressMandatory = "sourceAddress: Source address is mandatory";
    private final String destinationAddressMandatory = "destinationAddress: Destination address is mandatory";
    private final String passengerIdMandatory = "passengerId: Passenger id is mandatory";
    private final String rideStateMandatory = "rideState: Ride state is mandatory";

    private final String idInvalid = "id: must be greater than or equal to 0";
    private final String sourceAddressInvalid = "sourceAddress: size must be between 10 and 255";
    private final String destinationAddressInvalid = "destinationAddress: size must be between 10 and 255";
    private final String passengerIdInvalid = "passengerId: must be greater than or equal to 0";
    private final String driverIdInvalid = "driverId: must be greater than or equal to 0";

    private final String offsetInvalid = "offset: must be greater than or equal to 0";
    private final String limitInvalid = "limit: must be greater than or equal to 1";
    private final String limitBig = "limit: must be less than or equal to 20";


    @Test
    void getPageRides_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRides(0,5);
    }

    @Test
    void getPageRides_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL).param("offset", "0").param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRides_withInvalidParams_thenReturns400() throws Exception {
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
    void getPageRides_withBigLimit_thenReturns400() throws Exception {
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
    void getPageRidesByDriverId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/driver/{driverId}", driverId))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByDriverId(driverId, 0,5);
    }

    @Test
    void getPageRidesByDriverId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/driver/{driverId}", driverId)
                        .param("offset", "0")
                        .param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByDriverId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/driver/{driverId}", driverId)
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
    void getPageRidesByDriverId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/driver/{driverId}", driverId)
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
    void getPageRidesByDriverId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/driver/{driverId}", invalidDriverId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRidesByPassengerId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/passenger/{passengerId}", passengerId))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByPassengerId(driverId, 0,5);
    }

    @Test
    void getPageRidesByPassengerId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/passenger/{passengerId}", passengerId)
                        .param("offset", "0")
                        .param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByPassengerId_withInvalidParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(offsetInvalid, limitInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/passenger/{passengerId}", passengerId)
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
    void getPageRidesByPassengerId_withBigLimit_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(limitBig));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/passenger/{passengerId}", driverId)
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
    void getPageRidesByPassengerId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/passenger/{passengerId}", invalidPassengerId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getRide_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/{rideId}", rideId))
                .andExpect(status().isOk());
    }

    @Test
    void getRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(rideService.getRideById(rideId)).thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL + "/{rideId}", rideId))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void getRide_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{rideId}", invalidRideId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRide_whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<RideRequestDto> rideCaptor = ArgumentCaptor.forClass(RideRequestDto.class);
        verify(rideService, times(1)).createRide(rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void createRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(rideService.createRide(rideRequestDto)).thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void createRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdMandatory, destinationAddressMandatory, sourceAddressMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL)
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
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(passengerIdInvalid, driverIdInvalid, destinationAddressInvalid, sourceAddressInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL)
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
        mockMvc.perform(put(URL + "/{rideId}", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{rideId}", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RideRequestDto> rideCaptor = ArgumentCaptor.forClass(RideRequestDto.class);

        verify(rideService, times(1)).updateRide(anyLong(), rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void updateRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(rideService.updateRide(1L, rideRequestDto))
                .thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void updateRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(rideStateMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}/state", rideId)
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
    void updateRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(rideStateMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}/state", rideId)
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
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}", invalidRideId)
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
        mockMvc.perform(put(URL + "/{rideId}/state", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRideState_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{rideId}/state", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<RideStateRequestDto> rideCaptor = ArgumentCaptor.forClass(RideStateRequestDto.class);

        verify(rideService, times(1)).setNewState(anyLong(), rideCaptor.capture());
        assertThat(rideCaptor.getValue()).isEqualTo(rideStateRequestDto);
    }

    @Test
    void updateRideState_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(rideService.setNewState(1L, rideStateRequestDto))
                .thenReturn(rideNewStateResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}/state", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideNewStateResponseDto));
    }

    @Test
    void updateRideState_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(rideStateMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{rideId}/state", rideId)
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