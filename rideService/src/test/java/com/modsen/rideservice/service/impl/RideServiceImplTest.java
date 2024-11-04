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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    private final Ride rideA = new Ride(1L,1L,1L,"source address", "destination address", RideState.CREATED,LocalDateTime.now(),1000);
    private final RideRequestDto rideARequestDto = new RideRequestDto(1L,1L,"source address", "destination address", RideState.CREATED,LocalDateTime.now(),1000);
    private final RideResponseDto rideAResponseDto = new RideResponseDto(1L, 1L, 1L, "source address", "destination address", RideState.CREATED,LocalDateTime.now(),1000);

    private final List<Ride> rideList = List.of(rideA);
    private final List<RideResponseDto> rideResponseDtoList = List.of(rideAResponseDto);

    private final DriverResponseDto driverResponseDto = new DriverResponseDto(1L, "Driver", "driver@mail.ru", "712345678");
    private final PassengerResponseDto passengerResponseDto = new PassengerResponseDto(1L, "Passenger", "passenger@mail.ru", "712345678");

    @Test
    void getAllRides() {
        //Arrange
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
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RideResponseDto> expected = new PageDto<>(offset, limit, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAll(pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRides(offset, limit);

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
        when(rideRepository.findAllByDriverId(anyLong())).thenReturn(rideList);
        when(rideListMapper.toRideResponseDtoList(rideList)).thenReturn(rideResponseDtoList);

        //Act
        List<RideResponseDto> actual = rideService.getAllRidesByDriverId(rideA.getDriverId());

        //Assert
        verify(rideRepository).findAllByDriverId(1L);
        verify(rideListMapper).toRideResponseDtoList(rideList);
        assertEquals(rideResponseDtoList.size(), actual.size());
        assertIterableEquals(rideResponseDtoList, actual);
    }

    @Test
    void getPageRidesByDriverId() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RideResponseDto> expected = new PageDto<>(offset, limit, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAllByDriverId(1L, pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRidesByDriverId(1L, offset, limit);

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
        when(rideRepository.findAllByPassengerId(anyLong())).thenReturn(rideList);
        when(rideListMapper.toRideResponseDtoList(rideList)).thenReturn(rideResponseDtoList);

        //Act
        List<RideResponseDto> actual = rideService.getRidesByPassengerId(rideA.getId());

        //Assert
        verify(rideRepository).findAllByPassengerId(1L);
        verify(rideListMapper).toRideResponseDtoList(rideList);
        assertEquals(rideResponseDtoList.size(), actual.size());
        assertIterableEquals(rideResponseDtoList, actual);
    }

    @Test
    void getPageRidesByPassengerId() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RideResponseDto> expected = new PageDto<>(offset, limit, 1, rideResponseDtoList.size(), rideResponseDtoList);
        Page<Ride> ridePage = new PageImpl<>(rideList, pageRequest, 1);
        when(rideRepository.findAllByPassengerId(1L, pageRequest)).thenReturn(ridePage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RideResponseDto> actual = rideService.getPageRidesByPassengerId(1L, offset, limit);

        //Assert
        verify(rideRepository).findAllByPassengerId(1L, pageRequest);
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
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(rideA));
        when(rideMapper.toRideResponseDto(rideA)).thenReturn(rideAResponseDto);

        //Act
        RideResponseDto actual = rideService.getRideById(rideA.getId());

        //Assert
        verify(rideRepository).findById(rideA.getId());
        verify(rideMapper).toRideResponseDto(rideA);
        assertEquals(rideAResponseDto, actual);
    }

    @Test
    void getRideById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.getRideById(rideA.getId()),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(rideA.getId());
    }

    @Test
    void createRide_ExistingDriverIdExistingPassengerId_ReturnsValidDto() {
        //Arrange
        when(passengerClientService.getPassengerById(anyLong())).thenReturn(passengerResponseDto);
        when(driverClientService.getDriverById(anyLong())).thenReturn(driverResponseDto);
        when(rideMapper.toRide(rideARequestDto)).thenReturn(rideA);
        when(rideRepository.save(rideA)).thenReturn(rideA);
        when(rideMapper.toRideResponseDto(rideA)).thenReturn(rideAResponseDto);

        //Act
        RideResponseDto actual = rideService.createRide(rideARequestDto);

        //Assert
        verify(passengerClientService).getPassengerById(rideARequestDto.passengerId());
        verify(driverClientService).getDriverById(rideARequestDto.driverId());
        verify(rideMapper).toRide(rideARequestDto);
        verify(rideRepository).save(rideA);
        verify(rideMapper).toRideResponseDto(rideA);
        assertEquals(rideAResponseDto, actual);
    }

    @Test
    void updateRide_ExistingIdExistingDriverIdExistingPassengerId_ReturnsValidDto() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(rideA));
        when(passengerClientService.getPassengerById(anyLong())).thenReturn(passengerResponseDto);
        when(driverClientService.getDriverById(anyLong())).thenReturn(driverResponseDto);
        when(rideRepository.save(rideA)).thenReturn(rideA);
        when(rideMapper.toRideResponseDto(rideA)).thenReturn(rideAResponseDto);

        //Act
        RideResponseDto actual = rideService.updateRide(rideA.getId(), rideARequestDto);

        //Assert
        verify(rideRepository).findById(rideA.getId());
        verify(passengerClientService).getPassengerById(rideARequestDto.passengerId());
        verify(driverClientService).getDriverById(rideARequestDto.driverId());
        verify(rideRepository).save(rideA);
        verify(rideMapper).toRideResponseDto(rideA);
        assertEquals(rideAResponseDto, actual);
    }

    @Test
    void updateRide_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.updateRide(rideA.getId(), rideARequestDto),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(rideA.getId());
    }

    @Test
    void setNewState_ExistingIdValidState_ReturnsValidDto() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(rideA));
        when(validateStateService.validateState(any(RideState.class), any(RideState.class))).thenReturn(true);
        when(rideRepository.save(rideA)).thenReturn(rideA);
        RideResponseDto rideAUpdatedResponseDto = new RideResponseDto(1L, 1L, 1L, "source address", "destination address", RideState.ACCEPTED, LocalDateTime.now(),1000);
        when(rideMapper.toRideResponseDto(rideA)).thenReturn(rideAUpdatedResponseDto);

        //Act
        RideResponseDto actual = rideService.setNewState(rideA.getId(), new RideStateRequestDto("accepted"));

        //Assert
        verify(rideRepository).findById(rideA.getId());
        verify(validateStateService).validateState(RideState.CREATED, RideState.ACCEPTED);
        verify(rideRepository).save(rideA);
        verify(rideMapper).toRideResponseDto(rideA);
        assertEquals(RideState.ACCEPTED, actual.rideState());
    }

    @Test
    void setNewState_ExistingIdInvalidState_ReturnsInvalidStateException() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(rideA));
        when(validateStateService.validateState(any(RideState.class), any(RideState.class))).thenReturn(false);

        //Act
        //Assert
        assertThrows(InvalidStateException.class,
                () -> rideService.setNewState(rideA.getId(), new RideStateRequestDto(RideState.ACCEPTED.getState())),
                AppConstants.STATE_VALUE_ERROR);
        verify(rideRepository).findById(rideA.getId());
    }

    @Test
    void setNewState_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> rideService.setNewState(rideA.getId(), new RideStateRequestDto(RideState.ACCEPTED.getState())),
                AppConstants.RIDE_NOT_FOUND);
        verify(rideRepository).findById(rideA.getId());
    }
}