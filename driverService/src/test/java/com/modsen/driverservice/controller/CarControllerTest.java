package com.modsen.driverservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.exception.ListErrorMessage;
import com.modsen.driverservice.service.CarService;
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

import static com.modsen.driverservice.util.TestData.EXCEEDED_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.EXCEEDED_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.LIMIT;
import static com.modsen.driverservice.util.TestData.LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.OFFSET;
import static com.modsen.driverservice.util.TestData.OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.URL_CAR;
import static com.modsen.driverservice.util.TestData.URL_CAR_DRIVER_ID;
import static com.modsen.driverservice.util.TestData.URL_CAR_ID;
import static com.modsen.driverservice.util.TestData.CAR_ID;
import static com.modsen.driverservice.util.TestData.DRIVER_ID;
import static com.modsen.driverservice.util.TestData.getCarRequestDto;
import static com.modsen.driverservice.util.TestData.getCarResponseDto;
import static com.modsen.driverservice.util.TestData.getEmptyCarRequestDto;
import static com.modsen.driverservice.util.TestData.getInvalidCarRequestDto;
import static com.modsen.driverservice.util.TestData.INSUFFICIENT_CAR_ID;
import static com.modsen.driverservice.util.ViolationData.carBrandInvalid;
import static com.modsen.driverservice.util.ViolationData.carBrandMandatory;
import static com.modsen.driverservice.util.ViolationData.carColorInvalid;
import static com.modsen.driverservice.util.ViolationData.carColorMandatory;
import static com.modsen.driverservice.util.ViolationData.carModelInvalid;
import static com.modsen.driverservice.util.ViolationData.carModelMandatory;
import static com.modsen.driverservice.util.ViolationData.carNumberInvalid;
import static com.modsen.driverservice.util.ViolationData.carNumberMandatory;
import static com.modsen.driverservice.util.ViolationData.driverIdInvalid;
import static com.modsen.driverservice.util.ViolationData.idInvalid;
import static com.modsen.driverservice.util.ViolationData.limitExceeded;
import static com.modsen.driverservice.util.ViolationData.limitInsufficient;
import static com.modsen.driverservice.util.ViolationData.offsetInsufficient;
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

@WebMvcTest(controllers = CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CarService carService;

    @Test
    void getPageCars_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_CAR))
                .andExpect(status().isOk());
        verify(carService, times(1)).getPageCars(OFFSET_VALUE, LIMIT_VALUE);
    }

    @Test
    void getPageCars_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_CAR)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageCars_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInsufficient, limitInsufficient));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR)
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
    void getPageCars_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitExceeded));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR)
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
    void getCar_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_CAR_ID, CAR_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getCar_whenValidInput_thenReturnsResponseDto() throws Exception {
        CarResponseDto carResponseDto = getCarResponseDto();
        when(carService.getCarById(CAR_ID)).thenReturn(carResponseDto);

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR_ID, CAR_ID))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void getCar_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR_ID, INSUFFICIENT_CAR_ID))
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
    void getPageCarsByDriverId_whenEmptyParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_CAR_DRIVER_ID, DRIVER_ID))
                .andExpect(status().isOk());

        verify(carService, times(1)).getPageCarsByDriverId(DRIVER_ID, 0,5);
    }

    @Test
    void getPageCarsByDriverId_whenValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL_CAR_DRIVER_ID, DRIVER_ID)
                        .param(OFFSET, OFFSET_VALUE.toString())
                        .param(LIMIT, LIMIT_VALUE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPageCarsByDriverId_whenInsufficientParams_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(offsetInsufficient, limitInsufficient));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR_DRIVER_ID, DRIVER_ID)
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
    void getPageCarsByDriverId_whenLimitExceeded_thenReturns400() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(limitExceeded));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR_DRIVER_ID, DRIVER_ID)
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
    void getPageCarsByDriverId_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL_CAR_DRIVER_ID, INSUFFICIENT_DRIVER_ID))
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
    void createCar_whenValidInput_thenReturns201() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();

        mockMvc.perform(post(URL_CAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createCar_whenValidInput_thenMapsToBusinessModel() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();

        mockMvc.perform(post(URL_CAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CarRequestDto> carCaptor = ArgumentCaptor.forClass(CarRequestDto.class);
        verify(carService, times(1)).addCar(carCaptor.capture());
        assertThat(carCaptor.getValue()).isEqualTo(carRequestDto);
    }

    @Test
    void createDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();
        CarResponseDto carResponseDto = getCarResponseDto();
        when(carService.addCar(carRequestDto)).thenReturn(carResponseDto);

        MvcResult mvcResult = mockMvc.perform(post(URL_CAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void createDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto emptyCarRequestDto = getEmptyCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(carBrandMandatory,carColorMandatory,carNumberMandatory, carModelMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL_CAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCarRequestDto)))
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
        CarRequestDto invalidCarRequestDto = getInvalidCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(carBrandInvalid, carColorInvalid, carModelInvalid, carNumberInvalid, driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL_CAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarRequestDto)))
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
    void updateCar_whenValidInput_thenReturns200() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();

        mockMvc.perform(put(URL_CAR_ID, CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCar_whenValidInput_thenMapsToBusinessModel() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();

        mockMvc.perform(put(URL_CAR_ID, CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CarRequestDto> carCaptor = ArgumentCaptor.forClass(CarRequestDto.class);
        verify(carService, times(1)).updateCar(anyLong(), carCaptor.capture());
        assertThat(carCaptor.getValue())
                .isEqualTo(carRequestDto);
    }

    @Test
    void updateDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        CarRequestDto carRequestDto = getCarRequestDto();
        CarResponseDto carResponseDto = getCarResponseDto();
        when(carService.updateCar(CAR_ID, carRequestDto)).thenReturn(carResponseDto);

        MvcResult mvcResult = mockMvc.perform(put(URL_CAR_ID, CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void updateCar_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto emptyCarRequestDto = getEmptyCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(carBrandMandatory,carColorMandatory,carNumberMandatory, carModelMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL_CAR_ID, CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCarRequestDto)))
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
    void updateCar_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        CarRequestDto invalidCarRequestDto = getInvalidCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(carBrandInvalid, carColorInvalid, carModelInvalid, carNumberInvalid, driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_CAR_ID, CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarRequestDto)))
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
        CarRequestDto carRequestDto = getCarRequestDto();
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(HttpStatus.BAD_REQUEST.value(),
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL_CAR_ID, INSUFFICIENT_CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
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
    void deleteCar_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL_CAR_ID, CAR_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDriver_whenInsufficientId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                List.of(idInvalid));

        MvcResult mvcResult = mockMvc.perform(delete(URL_CAR_ID, INSUFFICIENT_CAR_ID))
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