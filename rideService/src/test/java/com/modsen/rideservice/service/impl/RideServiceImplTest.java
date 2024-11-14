package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.driver.DriverClientService;
import com.modsen.rideservice.client.passenger.PassengerClientService;
import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.exception.InvalidStateException;
import com.modsen.rideservice.exception.NotFoundException;
import com.modsen.rideservice.mapper.PageMapper;
import com.modsen.rideservice.mapper.RideListMapper;
import com.modsen.rideservice.mapper.RideMapper;
import com.modsen.rideservice.model.Ride;
import com.modsen.rideservice.model.RideState;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.util.AppConstants;
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

import static com.modsen.rideservice.util.TestData.DRIVER_ID;
import static com.modsen.rideservice.util.TestData.LIMIT_VALUE;
import static com.modsen.rideservice.util.TestData.OFFSET_VALUE;
import static com.modsen.rideservice.util.TestData.PASSENGER_ID;
import static com.modsen.rideservice.util.TestData.RIDE_ID;
import static com.modsen.rideservice.util.TestData.getDriverResponseDto;
import static com.modsen.rideservice.util.TestData.getPassengerResponseDto;
import static com.modsen.rideservice.util.TestData.getRide;
import static com.modsen.rideservice.util.TestData.getRideList;
import static com.modsen.rideservice.util.TestData.getRideRequestDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDto;
import static com.modsen.rideservice.util.TestData.getRideResponseDtoBuilder;
import static com.modsen.rideservice.util.TestData.getRideResponseDtoList;
import static com.modsen.rideservice.util.TestData.getRideStateRequestDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {
    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private RideListMapper rideListMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private ValidateStateService validateStateService;

    @Mock
    private PassengerClientService passengerClientService;

    @Mock
    private DriverClientService driverClientService;

    @Mock
    private RideCostService rideCostService;

    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void getAllRides() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        when(rideRepository.findAll()).thenReturn(rideList);
        when(rideListMapper.toRideResponseDtoList(rideList)).thenReturn(rideResponseDtoList);

        //Act
        List<RideResponseDto> actual = rideService.getAllRides();

        //Assert
        verify(rideRepository).findAll();
        verify(rideListMapper).toRideResponseDtoList(rideList);
        assertEquals(actual.size(), rideResponseDtoList.size());
        assertIterableEquals(actual, rideResponseDtoList);
    }

    @Test
    void getPageRides() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RideResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAll(pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRides(OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(rideRepository).findAll(pageRequest);
        verify(rideMapper, times(actual.content().size())).toRideResponseDto(any(Ride.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(actual.content(), expected.content());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(actual.pageSize(), expected.pageSize());
        assertEquals(actual.totalElements(), expected.totalElements());
        assertEquals(actual.totalPages(), expected.totalPages());
    }

    @Test
    void getAllRidesByDriverId() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        when(rideRepository.findAllByDriverId(anyLong())).thenReturn(rideList);
        when(rideListMapper.toRideResponseDtoList(rideList)).thenReturn(rideResponseDtoList);

        //Act
        List<RideResponseDto> actual = rideService.getAllRidesByDriverId(DRIVER_ID);

        //Assert
        verify(rideRepository).findAllByDriverId(DRIVER_ID);
        verify(rideListMapper).toRideResponseDtoList(rideList);
        assertEquals(rideResponseDtoList.size(), actual.size());
        assertIterableEquals(rideResponseDtoList, actual);
    }

    @Test
    void getPageRidesByDriverId() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RideResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAllByDriverId(DRIVER_ID, pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRidesByDriverId(DRIVER_ID, OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(rideRepository).findAllByDriverId(1L, pageRequest);
        verify(rideMapper, times(actual.content().size())).toRideResponseDto(any(Ride.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(expected.content(), actual.content());
        assertEquals(expected.pageNumber(), actual.pageNumber());
        assertEquals(expected.pageSize(), actual.pageSize());
        assertEquals(expected.totalElements(), actual.totalElements());
        assertEquals(expected.totalPages(), actual.totalPages());
    }

    @Test
    void getRidesByPassengerId() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        when(rideRepository.findAllByPassengerId(anyLong())).thenReturn(rideList);
        when(rideListMapper.toRideResponseDtoList(rideList)).thenReturn(rideResponseDtoList);

        //Act
        List<RideResponseDto> actual = rideService.getRidesByPassengerId(PASSENGER_ID);

        //Assert
        verify(rideRepository).findAllByPassengerId(PASSENGER_ID);
        verify(rideListMapper).toRideResponseDtoList(rideList);
        assertEquals(rideResponseDtoList.size(), actual.size());
        assertIterableEquals(rideResponseDtoList, actual);
    }

    @Test
    void getPageRidesByPassengerId() {
        //Arrange
        List<Ride> rideList = getRideList();
        List<RideResponseDto> rideResponseDtoList = getRideResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RideResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAllByPassengerId(PASSENGER_ID, pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRidesByPassengerId(PASSENGER_ID, OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(rideRepository).findAllByPassengerId(PASSENGER_ID, pageRequest);
        verify(rideMapper, times(actual.content().size())).toRideResponseDto(any(Ride.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(expected.content(), actual.content());
        assertEquals(expected.pageNumber(), actual.pageNumber());
        assertEquals(expected.pageSize(), actual.pageSize());
        assertEquals(expected.totalElements(), actual.totalElements());
        assertEquals(expected.totalPages(), actual.totalPages());
    }

    @Test
    void getRideById_ExistingId_ReturnsValidDto() {
        //Arrange
        Ride ride = getRide();
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(ride));
        when(rideMapper.toRideResponseDto(ride)).thenReturn(rideResponseDto);

        //Act
        RideResponseDto actual = rideService.getRideById(ride.getId());

        //Assert
        verify(rideRepository).findById(ride.getId());
        verify(rideMapper).toRideResponseDto(ride);
        assertEquals(rideResponseDto, actual);
    }

    @Test
    void getRideById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.getRideById(RIDE_ID),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(RIDE_ID);
    }

    @Test
    void createRide_ExistingDriverIdExistingPassengerId_ReturnsValidDto() {
        //Arrange
        Ride ride = getRide();
        RideRequestDto rideRequestDto = getRideRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(passengerClientService.getPassengerById(anyLong())).thenReturn(passengerResponseDto);
        when(driverClientService.getDriverById(anyLong())).thenReturn(driverResponseDto);
        when(rideMapper.toRide(rideRequestDto)).thenReturn(ride);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponseDto(ride)).thenReturn(rideResponseDto);

        //Act
        RideResponseDto actual = rideService.createRide(rideRequestDto);

        //Assert
        verify(passengerClientService).getPassengerById(rideRequestDto.passengerId());
        verify(driverClientService).getDriverById(rideRequestDto.driverId());
        verify(rideMapper).toRide(rideRequestDto);
        verify(rideRepository).save(ride);
        verify(rideMapper).toRideResponseDto(ride);
        assertEquals(rideResponseDto, actual);
    }

    @Test
    void updateRide_ExistingIdExistingDriverIdExistingPassengerId_ReturnsValidDto() {
        //Arrange
        Ride ride = getRide();
        RideRequestDto rideRequestDto = getRideRequestDto();
        RideResponseDto rideResponseDto = getRideResponseDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(ride));
        when(passengerClientService.getPassengerById(anyLong())).thenReturn(passengerResponseDto);
        when(driverClientService.getDriverById(anyLong())).thenReturn(driverResponseDto);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponseDto(ride)).thenReturn(rideResponseDto);

        //Act
        RideResponseDto actual = rideService.updateRide(ride.getId(), rideRequestDto);

        //Assert
        verify(rideRepository).findById(ride.getId());
        verify(passengerClientService).getPassengerById(rideRequestDto.passengerId());
        verify(driverClientService).getDriverById(rideRequestDto.driverId());
        verify(rideRepository).save(ride);
        verify(rideMapper).toRideResponseDto(ride);
        assertEquals(rideResponseDto, actual);
    }

    @Test
    void updateRide_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        RideRequestDto rideRequestDto = getRideRequestDto();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.updateRide(RIDE_ID, rideRequestDto),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(RIDE_ID);
    }

    @Test
    void setNewState_ExistingIdValidState_ReturnsValidDto() {
        //Arrange
        Ride ride = getRide();
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        RideResponseDto rideUpdatedResponseDto = getRideResponseDtoBuilder().rideState(RideState.ACCEPTED).build();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(ride));
        when(validateStateService.validateState(any(RideState.class), any(RideState.class))).thenReturn(true);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponseDto(ride)).thenReturn(rideUpdatedResponseDto);

        //Act
        RideResponseDto actual = rideService.setNewState(RIDE_ID, rideStateRequestDto);

        //Assert
        verify(rideRepository).findById(RIDE_ID);
        verify(validateStateService).validateState(RideState.CREATED, RideState.ACCEPTED);
        verify(rideRepository).save(ride);
        verify(rideMapper).toRideResponseDto(ride);
        assertEquals(RideState.ACCEPTED, actual.rideState());
    }

    @Test
    void setNewState_ExistingIdInvalidState_ReturnsInvalidStateException() {
        //Arrange
        Ride ride = getRide();
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(ride));
        when(validateStateService.validateState(any(RideState.class), any(RideState.class))).thenReturn(false);

        //Act
        //Assert
        assertThrows(InvalidStateException.class,
                () -> rideService.setNewState(ride.getId(), rideStateRequestDto),
                AppConstants.STATE_VALUE_ERROR);
        verify(rideRepository).findById(ride.getId());
    }

    @Test
    void setNewState_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        RideStateRequestDto rideStateRequestDto = getRideStateRequestDto();
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.setNewState(RIDE_ID, rideStateRequestDto),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(RIDE_ID);
    }
}