package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.util.AppConstants;
import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
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
import java.util.UUID;


import static com.modsen.driverservice.util.TestData.CAR_ID;
import static com.modsen.driverservice.util.TestData.DRIVER_ID;
import static com.modsen.driverservice.util.TestData.LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.getCar;
import static com.modsen.driverservice.util.TestData.getCarBuilder;
import static com.modsen.driverservice.util.TestData.getCarList;
import static com.modsen.driverservice.util.TestData.getCarRequestDto;
import static com.modsen.driverservice.util.TestData.getCarResponseDto;
import static com.modsen.driverservice.util.TestData.getCarResponseDtoList;
import static com.modsen.driverservice.util.TestData.getDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void getAllCars() {
        //Arrange
        List<Car> carList = getCarList();
        List<CarResponseDto> carResponseDtoList = getCarResponseDtoList();
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
        List<Car> carList = getCarList();
        List<CarResponseDto> carResponseDtoList = getCarResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<CarResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, carResponseDtoList.size(), carResponseDtoList);
        Page<Car> carPage = new PageImpl<>(carList, pageRequest, 1);
        when(carRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(carPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<CarResponseDto> actual = carService.getPageCars(OFFSET_VALUE, LIMIT_VALUE);

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
        List<Car> carList = getCarList();
        List<CarResponseDto> carResponseDtoList = getCarResponseDtoList();
        when(carRepository.findAllByDriverIdAndDeletedIsFalse(any(UUID.class))).thenReturn(carList);
        when(carListMapper.toCarResponseDTOList(carList)).thenReturn(carResponseDtoList);

        //Act
        List<CarResponseDto> actual = carService.getAllCarsByDriverId(DRIVER_ID);

        //Assert
        verify(carRepository).findAllByDriverIdAndDeletedIsFalse(DRIVER_ID);
        verify(carListMapper).toCarResponseDTOList(carList);
        assertEquals(carResponseDtoList.size(), actual.size());
        assertIterableEquals(carResponseDtoList, actual);
    }

    @Test
    void getCarById_ExistingId_ReturnsValidDto() {
        //Arrange
        Car car = getCar();
        CarResponseDto carResponseDto = getCarResponseDto();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(car));
        when(carMapper.toCarResponseDTO(car)).thenReturn(carResponseDto);

        //Act
        CarResponseDto actual = carService.getCarById(car.getId());

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
        verify(carMapper).toCarResponseDTO(car);
        assertEquals(carResponseDto, actual);
    }

    @Test
    void getCarById_NonExistingId_ThrowNotFoundException() {
        //Arrange
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.getCarById(CAR_ID),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(CAR_ID);
    }

    @Test
    void getCarByNumber_ExistingNumber_ReturnsValidDto() {
        //Arrange
        Car car = getCar();
        CarResponseDto carResponseDto = getCarResponseDto();
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(car));
        when(carMapper.toCarResponseDTO(car)).thenReturn(carResponseDto);

        //Act
        CarResponseDto actual = carService.getCarByNumber(car.getNumber());

        //Assert
        verify(carRepository).findByNumberAndDeletedIsFalse(car.getNumber());
        assertEquals(carResponseDto, actual);
    }

    @Test
    void getCarByNumber_NonExistingNumber_ThrowNotFoundException() {
        //Arrange
        Car car = getCar();
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.getCarByNumber(car.getNumber()),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByNumberAndDeletedIsFalse(car.getNumber());
    }

    @Test
    void addCar_ExistingDriverIdUniqueNumber_ReturnsValidDto() {
        //Arrange
        Car car = getCar();
        CarRequestDto carRequestDto = getCarRequestDto();
        Driver driver = getDriver();
        CarResponseDto carResponseDto = getCarResponseDto();

        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(carRepository.existsByNumberAndDeletedIsFalse(anyString())).thenReturn(false);
        when(carMapper.toCar(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarResponseDTO(car)).thenReturn(carResponseDto);

        //Act
        CarResponseDto actual = carService.addCar(carRequestDto);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
        verify(carRepository).existsByNumberAndDeletedIsFalse(carRequestDto.number());
        verify(carMapper).toCar(carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toCarResponseDTO(car);
        assertEquals(carResponseDto, actual);
    }

    @Test
    void addCar_NonExistingDriverId_ThrowNotFoundException() {
        //Arrange
        CarRequestDto carRequestDto = getCarRequestDto();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
       //Assert
        assertThrows(NotFoundException.class,
                () -> carService.addCar(carRequestDto),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
    }

    @Test
    void addCar_NonUniqueNumber_ThrowDuplicateFieldException() {
        //Arrange
        CarRequestDto carRequestDto = getCarRequestDto();
        Driver driver = getDriver();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(carRepository.existsByNumberAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> carService.addCar(carRequestDto),
                AppConstants.CAR_NUMBER_EXIST);
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
        verify(carRepository).existsByNumberAndDeletedIsFalse(carRequestDto.number());
    }

    @Test
    void updateCar_ExistingIdExistingDriverIdUniqueNumber_ReturnsValidDto() {
        //Arrange
        Car car = getCar();
        CarRequestDto carRequestDto = getCarRequestDto();
        CarResponseDto carResponseDto = getCarResponseDto();
        Driver driver = getDriver();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(car));
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarResponseDTO(car)).thenReturn(carResponseDto);

        //Act
        CarResponseDto actual = carService.updateCar(car.getId(), carRequestDto);

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
        verify(carRepository).findByNumberAndDeletedIsFalse(carRequestDto.number());
        verify(carRepository).save(car);
        verify(carMapper).toCarResponseDTO(car);
        assertEquals(carResponseDto, actual);
    }

    @Test
    void updateCar_NonExistingId_ThrowNotFoundException() {
        //Arrange
        Car car = getCar();
        CarRequestDto carRequestDto = getCarRequestDto();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.updateCar(car.getId(), carRequestDto),
                AppConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
    }

    @Test
    void updateCar_NonExistingDriverId_ThrowNotFoundException() {
        //Arrange
        Car car = getCar();
        CarRequestDto carRequestDto = getCarRequestDto();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(car));
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> carService.updateCar(car.getId(), carRequestDto),
                AppConstants.DRIVER_NOT_FOUND);
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
    }

    @Test
    void updateCar_NonUniqueNumber_ThrowDuplicateFieldException() {
        //Arrange
        Car car = getCar();
        CarRequestDto carRequestDto = getCarRequestDto();
        Driver driver = getDriver();
        Car carWithSameNumber = getCarBuilder()
                .id(2L)
                .build();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(car));
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(carRepository.findByNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(carWithSameNumber));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> carService.updateCar(car.getId(), carRequestDto),
                AppConstants.CAR_NUMBER_EXIST);
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
        verify(driverRepository).findByIdAndDeletedIsFalse(UUID.fromString(carRequestDto.driverId()));
        verify(carRepository).findByNumberAndDeletedIsFalse(carRequestDto.number());
    }

    @Test
    void deleteCar_ExistingId() {
        //Arrange
        Car car = getCar();
        when(carRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);

        //Act
        carService.deleteCar(car.getId());

        //Assert
        verify(carRepository).findByIdAndDeletedIsFalse(car.getId());
        verify(carRepository).save(car);
        assertTrue(car.getDeleted());
    }

    @Test
    void deleteCar_NonExistingId_ThrowNotFoundException() {
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