package com.modsen.driverservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.exception.ListErrorMessage;
import com.modsen.driverservice.service.CarService;
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

@WebMvcTest(controllers = CarController.class)
class CarControllerTest {
    private final CarRequestDto carRequestDto = new CarRequestDto("red", "sedan", "audi", "12345", 1L);
    private final CarRequestDto emptyCarRequestDto = new CarRequestDto(null, null, null, null, null);
    private final CarRequestDto invalidCarRequestDto = new CarRequestDto("r", "s", "a", "1", -1L);
    private final DriverResponseDto driverResponseDto = new DriverResponseDto(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0);
    private final CarResponseDto carResponseDto = new CarResponseDto(1L, "red", "sedan", "audi", "12345", driverResponseDto);

    private final Long carId = 1L;
    private final Long invalidCarId = -1L;
    private final Long driverId = 1L;
    private final Long invalidDriverId = -1L;

    private final String URL = "/api/v1/cars";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CarService carService;

    private final String carModelMandatory = "model: Model is mandatory";
    private final String carColorMandatory = "color: Color is mandatory";
    private final String carBrandMandatory = "brand: Brand is mandatory";
    private final String carNumberMandatory = "number: Number is mandatory";

    private final String carModelInvalid = "model: size must be between 2 and 50";
    private final String carColorInvalid = "color: size must be between 2 and 50";
    private final String carBrandInvalid = "brand: size must be between 2 and 50";
    private final String carNumberInvalid = "number: size must be between 2 and 20";
    private final String driverIdInvalid = "driverId: must be greater than or equal to 0";

    private final String carIdInvalid = "id: must be greater than or equal to 0";
    private final String offsetInvalid = "offset: must be greater than or equal to 0";
    private final String limitInvalid = "limit: must be greater than or equal to 1";
    private final String limitBig = "limit: must be less than or equal to 20";

    @Test
    void getPageCars_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(carService, times(1)).getPageCars(0,5);
    }

    @Test
    void getPageCars_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL).param("offset", "0").param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageCars_withInvalidParams_thenReturns400() throws Exception {
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
    void getPageCars_withBigLimit_thenReturns400() throws Exception {
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
    void getCar_whenValidId_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/{carId}", carId))
                .andExpect(status().isOk());
    }

    @Test
    void getCar_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(carService.getCarById(carId)).thenReturn(carResponseDto);
        MvcResult mvcResult = mockMvc.perform(get(URL + "/{carId}", carId))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void getCar_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carIdInvalid));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{carId}", invalidCarId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void getPageCarsByDriverId_withoutParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/driver/{driverId}", driverId))
                .andExpect(status().isOk());
        verify(carService, times(1)).getPageCarsByDriverId(driverId, 0,5);
    }

    @Test
    void getPageCarsByDriverId_withValidParams_thenReturns201() throws Exception {
        mockMvc.perform(get(URL + "/driver/{driverId}", driverId)
                        .param("offset", "0")
                        .param("limit", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageCarsByDriverId_withInvalidParams_thenReturns400() throws Exception {
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
    void getPageCarsByDriverId_withBigLimit_thenReturns400() throws Exception {
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
    void getPageCarsByDriverId_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
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
    void createCar_whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createCar_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CarRequestDto> carCaptor = ArgumentCaptor.forClass(CarRequestDto.class);
        verify(carService, times(1)).addCar(carCaptor.capture());
        assertThat(carCaptor.getValue()).isEqualTo(carRequestDto);
    }

    @Test
    void createDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(carService.addCar(carRequestDto)).thenReturn(carResponseDto);
        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void createDriver_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carBrandMandatory,carColorMandatory,carNumberMandatory, carModelMandatory));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCarRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void createDriver_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carBrandInvalid, carColorInvalid, carModelInvalid, carNumberInvalid, driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateCar_whenValidInput_thenReturns200() throws Exception {
        mockMvc.perform(put(URL + "/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCar_whenValidInput_thenMapsToBusinessModel() throws Exception {
        mockMvc.perform(put(URL + "/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CarRequestDto> carCaptor = ArgumentCaptor.forClass(CarRequestDto.class);

        verify(carService, times(1)).updateCar(anyLong(), carCaptor.capture());
        assertThat(carCaptor.getValue()).isEqualTo(carRequestDto);
    }

    @Test
    void updateDriver_whenValidInput_thenReturnsResponseDto() throws Exception {
        when(carService.updateCar(1L, carRequestDto)).thenReturn(carResponseDto);
        MvcResult mvcResult = mockMvc.perform(put(URL + "/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(carResponseDto));
    }

    @Test
    void updateCar_whenEmptyValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carBrandMandatory,carColorMandatory,carNumberMandatory, carModelMandatory));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCarRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void updateCar_whenInvalidValue_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carBrandInvalid, carColorInvalid, carModelInvalid, carNumberInvalid, driverIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarRequestDto)))
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
                List.of(carIdInvalid));

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{carId}", invalidCarId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }

    @Test
    void deleteCar_whenValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete(URL + "/{carId}", carId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDriver_whenInvalidId_thenReturns400AndErrorResult() throws Exception {
        ListErrorMessage expectedErrorResponse = new ListErrorMessage(400,
                List.of(carIdInvalid));

        MvcResult mvcResult = mockMvc.perform(delete(URL + "/{carId}", invalidCarId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        ListErrorMessage actualErrorResponse = objectMapper.readValue(actualResponseBody, ListErrorMessage.class);

        assertThat(actualErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(actualErrorResponse.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrorResponse.errorMessages());
    }


}