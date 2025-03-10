package com.modsen.rideservice.service.impl;

import com.modsen.exceptionstarter.exception.InvalidStateException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.rideservice.client.driver.DriverClientService;
import com.modsen.rideservice.client.passenger.PassengerClientService;
import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.mapper.PageMapper;
import com.modsen.rideservice.mapper.RideListMapper;
import com.modsen.rideservice.mapper.RideMapper;
import com.modsen.rideservice.model.Ride;
import com.modsen.rideservice.model.RideState;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.RideService;
import com.modsen.rideservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.modsen.rideservice.util.AppConstants.RIDE_CACHE_NAME;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final RideListMapper rideListMapper;
    private final PageMapper pageMapper;
    private final ValidateStateService validateStateService;
    private final RideCostService rideCostService;
    private final PassengerClientService passengerClientService;
    private final DriverClientService driverClientService;

    @Override
    public List<RideResponseDto> getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        return rideListMapper.toRideResponseDtoList(rides);
    }

    @Override
    public PageDto<RideResponseDto> getPageRides(Integer offset, Integer limit) {
        Page<RideResponseDto> pageRides = rideRepository.findAll(PageRequest.of(offset, limit))
                .map(rideMapper::toRideResponseDto);
        return pageMapper.pageToDto(pageRides);
    }

    @Override
    public List<RideResponseDto> getAllRidesByDriverId(UUID driverId) {
        List<Ride> rides = rideRepository.findAllByDriverId(driverId);
        return rideListMapper.toRideResponseDtoList(rides);
    }

    @Override
    public PageDto<RideResponseDto> getPageRidesByDriverId(UUID driverId, Integer offset, Integer limit) {
        Page<RideResponseDto> rides = rideRepository.findAllByDriverId(driverId, PageRequest.of(offset, limit))
                .map(rideMapper::toRideResponseDto);
        return pageMapper.pageToDto(rides);
    }

    @Override
    public List<RideResponseDto> getRidesByPassengerId(UUID passengerId) {
        List<Ride> rides = rideRepository.findAllByPassengerId(passengerId);
        return rideListMapper.toRideResponseDtoList(rides);
    }

    @Override
    public PageDto<RideResponseDto> getPageRidesByPassengerId(UUID passengerId, Integer offset, Integer limit) {
        Page<RideResponseDto> rides = rideRepository.findAllByPassengerId(passengerId, PageRequest.of(offset, limit))
                .map(rideMapper::toRideResponseDto);
        return pageMapper.pageToDto(rides);
    }

    @Override
    @Cacheable(value = RIDE_CACHE_NAME, key = "#id")
    public RideResponseDto getRideById(Long id) {
        Ride ride = findByIdOrThrow(id);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    @CachePut(value = RIDE_CACHE_NAME, key = "#result.id()")
    public RideResponseDto createRide(RideCreateRequestDto rideRequestDto, String bearerToken) {
        PassengerResponseDto passengerResponseDto = passengerClientService.getPassengerById(rideRequestDto.passengerId(), bearerToken);
        Ride rideToSave = rideMapper.toRide(rideRequestDto);
        rideToSave.setRideState(RideState.CREATED);
        rideToSave.setRideDateTime(LocalDateTime.now());
        rideToSave.setRideCost(rideCostService.getRideCost());
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @CachePut(value = RIDE_CACHE_NAME, key = "#id")
    public RideResponseDto acceptRide(Long id, RideAcceptRequestDto rideRequestDto, String bearerToken) {
        Ride rideToSave = findByIdOrThrow(id);
        DriverResponseDto driverResponseDto = driverClientService.getDriverById(rideRequestDto.driverId(), bearerToken);
        rideToSave.setRideState(RideState.ACCEPTED);
        rideToSave.setDriverId(UUID.fromString(rideRequestDto.driverId()));
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @CachePut(value = RIDE_CACHE_NAME, key = "#id")
    public RideResponseDto cancelRide(Long id, String bearerToken) {
        Ride rideToSave = findByIdOrThrow(id);
        rideToSave.setRideState(RideState.CANCELLED);
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    @CachePut(value = RIDE_CACHE_NAME, key = "#id")
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto, String bearerToken) {
        Ride rideToSave = findByIdOrThrow(id);
        PassengerResponseDto passengerResponseDto = passengerClientService.getPassengerById(rideRequestDto.passengerId(), bearerToken);
        if(rideRequestDto.driverId() != null) {
            DriverResponseDto driverResponseDto = driverClientService.getDriverById(rideRequestDto.driverId(), bearerToken);
        }
        rideMapper.updateRide(rideToSave, rideRequestDto);
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    @CachePut(value = RIDE_CACHE_NAME, key = "#id")
    public RideResponseDto setNewState(Long id, RideStateRequestDto newStateDto) {
        Ride rideToSave = findByIdOrThrow(id);
        RideState newState = RideState.fromValue(newStateDto.rideState());
        if (validateStateService.validateState(rideToSave.getRideState(), newState)) {
            rideToSave.setRideState(newState);
            Ride ride = rideRepository.save(rideToSave);
            return rideMapper.toRideResponseDto(ride);
        }
        throw new InvalidStateException(MessageConstants.STATE_VALUE_ERROR);
    }

    private Ride findByIdOrThrow(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageConstants.RIDE_NOT_FOUND));
    }
}
