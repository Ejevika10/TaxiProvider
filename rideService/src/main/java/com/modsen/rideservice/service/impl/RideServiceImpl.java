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
import com.modsen.rideservice.service.RideService;
import com.modsen.rideservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<RideResponseDto> getAllRidesByDriverId(Long driverId) {
        List<Ride> rides = rideRepository.findAllByDriverId(driverId);
        return rideListMapper.toRideResponseDtoList(rides);
    }

    @Override
    public PageDto<RideResponseDto> getPageRidesByDriverId(Long driverId, Integer offset, Integer limit) {
        Page<RideResponseDto> rides = rideRepository.findAllByDriverId(driverId, PageRequest.of(offset, limit))
                .map(rideMapper::toRideResponseDto);
        return pageMapper.pageToDto(rides);
    }

    @Override
    public List<RideResponseDto> getRidesByPassengerId(Long passengerId) {
        List<Ride> rides = rideRepository.findAllByPassengerId(passengerId);
        return rideListMapper.toRideResponseDtoList(rides);
    }

    @Override
    public PageDto<RideResponseDto> getPageRidesByPassengerId(Long passengerId, Integer offset, Integer limit) {
        Page<RideResponseDto> rides = rideRepository.findAllByPassengerId(passengerId, PageRequest.of(offset, limit))
                .map(rideMapper::toRideResponseDto);
        return pageMapper.pageToDto(rides);
    }

    @Override
    public RideResponseDto getRideById(Long id) {
        Ride ride = findByIdOrThrow(id);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        PassengerResponseDto passengerResponseDto = passengerClientService.getPassengerById(rideRequestDto.passengerId());
        if(rideRequestDto.driverId() != null) {
            DriverResponseDto driverResponseDto = driverClientService.getDriverById(rideRequestDto.driverId());
        }
        Ride rideToSave = rideMapper.toRide(rideRequestDto);
        rideToSave.setRideState(RideState.CREATED);
        rideToSave.setRideDateTime(LocalDateTime.now());
        rideToSave.setRideCost(rideCostService.getRideCost());
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        Ride rideToSave = findByIdOrThrow(id);
        PassengerResponseDto passengerResponseDto = passengerClientService.getPassengerById(rideRequestDto.passengerId());
        if(rideRequestDto.driverId() != null) {
            DriverResponseDto driverResponseDto = driverClientService.getDriverById(rideRequestDto.driverId());
        }
        rideMapper.updateRide(rideToSave, rideRequestDto);
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    public RideResponseDto setNewState(Long id, RideStateRequestDto newStateDto) {
        Ride rideToSave = findByIdOrThrow(id);
        RideState newState = RideState.fromValue(newStateDto.rideState());
        if (validateStateService.validateState(rideToSave.getRideState(), newState)) {
            rideToSave.setRideState(newState);
            Ride ride = rideRepository.save(rideToSave);
            return rideMapper.toRideResponseDto(ride);
        }
        throw new InvalidStateException(AppConstants.STATE_VALUE_ERROR);
    }

    private Ride findByIdOrThrow(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.RIDE_NOT_FOUND));
    }
}
