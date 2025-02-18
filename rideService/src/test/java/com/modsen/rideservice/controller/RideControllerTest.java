package com.modsen.rideservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.GlobalExceptionHandler;
import com.modsen.exceptionstarter.message.ListErrorMessage;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.model.RideState;
import com.modsen.rideservice.service.RideService;
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

import static com.modsen.rideservice.util.TestData.AUTHORIZATION_VALUE;
import static com.modsen.rideservice.util.TestData.DRIVER_ID;
import static com.modsen.rideservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.INSUFFICIENT_RIDE_ID;
import static com.modsen.rideservice.util.TestData.INVALID_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.INVALID_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.LIMIT;
import static com.modsen.rideservice.util.TestData.LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.OFFSET;
import static com.modsen.rideservice.util.TestData.OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.RIDE_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_DRIVER_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_ACCEPT;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_CANCEL;
import static com.modsen.rideservice.util.TestData.URL_RIDE_ID_STATE;
import static com.modsen.rideservice.util.TestData.URL_RIDE_PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.getEmptyRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getEmptyRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getEmptyRideRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getInvalidRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideAcceptRequestDto;
import static com.modsen.rideservice.util.TestData.getRideCreateRequestDto;
import static com.modsen.rideservice.util.TestData.getRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDtoBuilder;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDto;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDtoBuilder;
import static com.modsen.rideservice.util.ViolationData.DESTINATION_ADDRESS_INVALID;
import static com.modsen.rideservice.util.ViolationData.DESTINATION_ADDRESS_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.DRIVER_ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.DRIVER_ID_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.LIMIT_EXCEEDED;
import static com.modsen.rideservice.util.ViolationData.LIMIT_INSUFFICIENT;
import static com.modsen.rideservice.util.ViolationData.OFFSET_INSUFFICIENT;
import static com.modsen.rideservice.util.ViolationData.PASSENGER_ID_INVALID;
import static com.modsen.rideservice.util.ViolationData.PASSENGER_ID_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.RIDE_STATE_MANDATORY;
import static com.modsen.rideservice.util.ViolationData.SOURCE_ADDRESS_INVALID;
import static com.modsen.rideservice.util.ViolationData.SOURCE_ADDRESS_MANDATORY;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RideController.class)
@WithMockUser
@Import(GlobalExceptionHandler.class)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    @Test
    void getPageRides_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRides(OFFSET_VALUE,LIMIT_VALUE);
    }

    @Test
    void getPageRides_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRides_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

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
    void getPageRides_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

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
    void getPageRidesByDriverId_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByDriverId(DRIVER_ID, OFFSET_VALUE, LIMIT_VALUE);

    }

    @Test
    void getPageRidesByDriverId_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_DRIVER_ID, DRIVER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByDriverId_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

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
    void getPageRidesByDriverId_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

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
                List.of(DRIVER_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_DRIVER_ID, INVALID_DRIVER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageRidesByPassengerId_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID))
                .andExpect(status().isOk());
        verify(rideService, times(1)).getPageRidesByPassengerId(PASSENGER_ID, OFFSET_VALUE,LIMIT_VALUE);

    }

    @Test
    void getPageRidesByPassengerId_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_RIDE_PASSENGER_ID, PASSENGER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageRidesByPassengerId_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(OFFSET_INSUFFICIENT, LIMIT_INSUFFICIENT));

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
    void getPageRidesByPassengerId_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(LIMIT_EXCEEDED));

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
                List.of(PASSENGER_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(get(URL_RIDE_PASSENGER_ID, INVALID_PASSENGER_ID))
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
    void getRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

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
        RideCreateRequestDto rideRequestDto = getRideCreateRequestDto();
        mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void createRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideCreateRequestDto rideRequestDto = getRideCreateRequestDto();
        mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated());

        ArgumentCaptor<RideCreateRequestDto> rideCaptor = ArgumentCaptor.forClass(RideCreateRequestDto.class);
        verify(rideService, times(1)).createRide(rideCaptor.capture(), anyString());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void createRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideCreateRequestDto rideRequestDto = getRideCreateRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideService.createRide(rideRequestDto, AUTHORIZATION_VALUE)).thenReturn(rideResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void createRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideCreateRequestDto emptyRideRequestDto = getEmptyRideCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_MANDATORY, DESTINATION_ADDRESS_MANDATORY, SOURCE_ADDRESS_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideCreateRequestDto invalidRideRequestDto = getInvalidRideCreateRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID, DESTINATION_ADDRESS_INVALID, SOURCE_ADDRESS_INVALID));

        MvcResult mvcResult = mockMvc.perform(post(URL_RIDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void acceptRide_whenValidInput_thenReturns200() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        mockMvc.perform(put(URL_RIDE_ID_ACCEPT, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideAcceptRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void acceptRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        mockMvc.perform(put(URL_RIDE_ID_ACCEPT, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideAcceptRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());

        ArgumentCaptor<RideAcceptRequestDto> rideCaptor = ArgumentCaptor.forClass(RideAcceptRequestDto.class);

        verify(rideService, times(1)).acceptRide(anyLong(), rideCaptor.capture(), anyString());
        assertThat(rideCaptor.getValue()).isEqualTo(rideAcceptRequestDto);
    }

    @Test
    void acceptRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.ACCEPTED)
                .build();
        when(rideService.acceptRide(RIDE_ID, rideAcceptRequestDto, AUTHORIZATION_VALUE))
                .thenReturn(rideResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_ACCEPT, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideAcceptRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void acceptRide_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto emptyRideAcceptRequestDto = getEmptyRideAcceptRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_ACCEPT, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideAcceptRequestDto))
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
    void acceptRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto invalidRideAcceptRequestDto = getInvalidRideAcceptRequestDto();

        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(DRIVER_ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_ACCEPT, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRideAcceptRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void acceptRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_ACCEPT, INSUFFICIENT_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideAcceptRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void cancelRide_whenValidInput_thenReturns200() throws Exception {
        mockMvc.perform(put(URL_RIDE_ID_CANCEL, RIDE_ID)
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void cancelRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideResponseDto rideResponseDto = getRideResponseDtoBuilder()
                .rideState(RideState.CANCELLED)
                .build();
        when(rideService.cancelRide(RIDE_ID, AUTHORIZATION_VALUE))
                .thenReturn(rideResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_CANCEL, RIDE_ID)
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(rideResponseDto));
    }

    @Test
    void cancelRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_CANCEL, INSUFFICIENT_RIDE_ID)
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void updateRide_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());

        ArgumentCaptor<RideRequestDto> rideCaptor = ArgumentCaptor.forClass(RideRequestDto.class);

        verify(rideService, times(1)).updateRide(anyLong(), rideCaptor.capture(), anyString());
        assertThat(rideCaptor.getValue()).isEqualTo(rideRequestDto);
    }

    @Test
    void updateRide_whenValidInput_thenReturnsResponseDto() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideService.updateRide(RIDE_ID, rideRequestDto, AUTHORIZATION_VALUE))
                .thenReturn(rideResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(PASSENGER_ID_MANDATORY, SOURCE_ADDRESS_MANDATORY, DESTINATION_ADDRESS_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideRequestDto))
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
    void updateRide_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto invalidRideRequestDto = getInvalidRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(PASSENGER_ID_INVALID, DRIVER_ID_INVALID, SOURCE_ADDRESS_INVALID, DESTINATION_ADDRESS_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateRide_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        RideRequestDto rideRequestDto = getRideRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(ID_INVALID));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID, INSUFFICIENT_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                        .content(objectMapper.writeValueAsString(rideStateRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void updateRideState_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideStateRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                        .content(objectMapper.writeValueAsString(rideStateRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
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
                List.of(RIDE_STATE_MANDATORY));

        MvcResult mvcResult = mockMvc.perform(put(URL_RIDE_ID_STATE, RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRideStateRequestDto))
                        .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }
}