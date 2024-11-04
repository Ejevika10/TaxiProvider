package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private  CarMapper carMapper;

    @Mock
    private  CarListMapper carListMapper;

    @Mock
    private  PageMapper pageMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private final Driver driverA = new Driver(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0, null, false);
    private final DriverResponseDto driverAResponseDto = new DriverResponseDto(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0);

    private final Car carA = new Car(1L, "red", "sedan", "audi", "12345A", false, driverA);
    private final CarRequestDto carARequestDto = new CarRequestDto("red", "sedan", "audi", "12345A", driverA.getId());
    private final CarResponseDto carAResponseDto = new CarResponseDto(1L, "red", "sedan", "audi", "12345A", driverAResponseDto);

    private final Car carB = new Car(2L, "red", "sedan", "audi", "12345B", false, driverA);
    private final CarRequestDto carBRequestDto = new CarRequestDto("red", "sedan", "audi", "12345B", driverA.getId());
    private final CarResponseDto carBResponseDto = new CarResponseDto(1L, "red", "sedan", "audi", "12345B", driverAResponseDto);

    private final List<Car> carList = List.of(carA, carB);
    private final List<CarResponseDto> carResponseDtoList = List.of(carAResponseDto, carBResponseDto);

    @Test
    void getAllCars() {
        //Arrange
        when(carRepository.findAllByDeletedIsFalse()).thenReturn(carList);
        when(carListMapper.toCarResponseDTOList(carList)).thenReturn(carResponseDtoList);

        //Act
        List<CarResponseDto> actual = carService.getAllCars();

        //Assert
        verify(carRepository).findAllByDeletedIsFalse();
        verify(carListMapper).toCarResponseDTOList(carList);
        assertEquals(carResponseDtoList.size(), actual.size());
        assertIterableEquals(carResponseDtoList, actual);
    }

    @Test
    void getPageCars() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<CarResponseDto> expected = new PageDto<>(offset, limit, 1, carResponseDtoList.size(), carResponseDtoList);
        Page<Car> carPage = new PageImpl<>(carList, pageRequest, 1);
        when(carRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(carPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<CarResponseDto> actual = carService.getPageCars(offset, limit);

        //Assert
        verify(carRepository).findAllByDeletedIsFalse(pageRequest);
        verify(carMapper, times(actual.content().size())).toCarResponseDTO(any(Car.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(actual.content(), expected.content());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(actual.pageSize(), expected.pageSize());
        assertEquals(actual.totalElements(), expected.totalElements());
        assertEquals(actual.totalPages(), expected.totalPages());
    }

    @Test
    void getAllCarsByDriverId() {
        //Arrange
        when(carRepository.findAllByDriverIdAndDeletedIsFalse(anyLong())).thenReturn(carList);
        when(carListMapper.toCarResponseDTOList(carList)).thenReturn(carResponseDtoList);

        //Act
        List<CarResponseDto> actual = carService.getAllCarsByDriverId(driverA.getId());

        //Assert
        verify(carRepository).findAllByDriverIdAndDeletedIsFalse(driverA.getId());
        verify(carListMapper).toCarResponseDTOList(carList);
        assertEquals(carResponseDtoList.size(), actual.size());
        assertIterableEquals(carResponseDtoList, actual);
    }

    @Test
    void getCarById_ExistingId_ReturnsValidDto() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(carA));
        when(carMapper.toCarResponseDTO(carA)).thenReturn(carAResponseDto);

        //Act
        CarResponseDto actual = carService.getCarById(carA.getId());

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
        verify(carMapper).toCarResponseDTO(carA);
        assertEquals(carAResponseDto, actual);
    }

    @Test
    void getCarById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.getCarById(1L),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void getCarByNumber_ExistingNumber_ReturnsValidDto() {
        //Arrange
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(carA));
        when(carMapper.toCarResponseDTO(carA)).thenReturn(carAResponseDto);

        //Act
        CarResponseDto actual = carService.getCarByNumber(carA.getNumber());

        //Assert
        verify(carRepository).findByNumberAndDeletedIsFalse(carA.getNumber());
        assertEquals(carAResponseDto, actual);
    }

    @Test
    void getCarByNumber_NonExistingNumber_ReturnsNotFoundException() {
        //Arrange
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.getCarByNumber(carA.getNumber()),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByNumberAndDeletedIsFalse(carA.getNumber());
    }

    @Test
    void addCar_ExistingDriverIdUniqueNumber_ReturnsValidDto() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(carRepository.existsByNumberAndDeletedIsFalse(anyString())).thenReturn(false);
        when(carMapper.toCar(carARequestDto)).thenReturn(carA);
        when(carRepository.save(carA)).thenReturn(carA);
        when(carMapper.toCarResponseDTO(carA)).thenReturn(carAResponseDto);

        //Act
        CarResponseDto actual = carService.addCar(carARequestDto);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(carARequestDto.driverId());
        verify(carRepository).existsByNumberAndDeletedIsFalse(carARequestDto.number());
        verify(carMapper).toCar(carARequestDto);
        verify(carRepository).save(carA);
        verify(carMapper).toCarResponseDTO(carA);
        assertEquals(carAResponseDto, actual);
    }

    @Test
    void addCar_NonExistingDriverId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
       //Assert
        assertThrows(NotFoundException.class,
                () -> carService.addCar(carARequestDto),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(carARequestDto.driverId());
    }

    @Test
    void addCar_NonUniqueNumber_ReturnsDuplicateFieldException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(carRepository.existsByNumberAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> carService.addCar(carARequestDto),
                AppConstants.CAR_NUMBER_EXIST);
        verify(driverRepository).findByIdAndDeletedIsFalse(carARequestDto.driverId());
        verify(carRepository).existsByNumberAndDeletedIsFalse(carARequestDto.number());
    }

    @Test
    void updateCar_ExistingIdExistingDriverIdUniqueNumber_ReturnsValidDto() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(carA));
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(carRepository.save(carA)).thenReturn(carA);
        when(carMapper.toCarResponseDTO(carA)).thenReturn(carAResponseDto);

        //Act
        CarResponseDto actual = carService.updateCar(carA.getId(), carARequestDto);

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(carARequestDto.driverId());
        verify(carRepository).findByNumberAndDeletedIsFalse(carARequestDto.number());
        verify(carRepository).save(carA);
        verify(carMapper).toCarResponseDTO(carA);
        assertEquals(carAResponseDto, actual);
    }

    @Test
    void updateCar_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.updateCar(carA.getId(), carARequestDto),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
    }

    @Test
    void updateCar_NonExistingDriverId_ReturnsNotFoundException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(carA));
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.updateCar(carA.getId(), carARequestDto),
                AppConstants.DRIVER_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(carARequestDto.driverId());
    }

    @Test
    void updateCar_NonUniqueNumber_ReturnsDuplicateFieldException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(carA));
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(carB));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> carService.updateCar(carA.getId(), carBRequestDto),
                AppConstants.CAR_NUMBER_EXIST);
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(carBRequestDto.driverId());
        verify(carRepository).findByNumberAndDeletedIsFalse(carBRequestDto.number());
    }

    @Test
    void deleteCar_ExistingId() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(carA));
        when(carRepository.save(carA)).thenReturn(carA);

        //Act
        carService.deleteCar(carA.getId());

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(carA.getId());
        verify(carRepository).save(carA);
        assertTrue(carA.getDeleted());
    }

    @Test
    void deleteCar_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.deleteCar(3L),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(3L);
    }
}