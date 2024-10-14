package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final RideListMapper rideListMapper;
    private final PageMapper pageMapper ;
    private final MessageSource messageSource;
    private final ValidateStateService validateStateService;

    private static final Random RANDOM = new Random();

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
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.RIDE_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        return rideMapper.toRideResponseDto(ride);
    }

    /*To Do: check if passenger with passengerId exists
    *       check if driver with driverId exists
    * */
    @Override
    @Transactional
    public RideResponseDto createRide(RideRequestDto rideRequestDto) {
        Ride rideToSave = rideMapper.toRide(rideRequestDto);
        rideToSave.setRideState(RideState.CREATED);
        rideToSave.setRideDateTime(LocalDateTime.now());
        rideToSave.setRideCost(getRideCost());
        Ride ride = rideRepository.save(rideToSave);
        return rideMapper.toRideResponseDto(ride);
    }

    @Override
    @Transactional
    public RideResponseDto updateRide(Long id, RideRequestDto rideRequestDto) {
        if(rideRepository.existsById(id)){
            Ride rideToSave = rideMapper.toRide(rideRequestDto);
            rideToSave.setId(id);
            Ride ride = rideRepository.save(rideToSave);
            return rideMapper.toRideResponseDto(ride);
        }
        throw new NotFoundException(
                messageSource.getMessage(AppConstants.RIDE_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @Override
    @Transactional
    public RideResponseDto setNewState(Long id, String newState) {
        Ride rideToSave = rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.RIDE_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        if(validateStateService.validateState(rideToSave.getRideState(), RideState.fromValue(newState))){
            rideToSave.setRideState(
                    RideState.fromValue(newState));
            Ride ride = rideRepository.save(rideToSave);
            return rideMapper.toRideResponseDto(ride);
        }
        throw new InvalidStateException(
                messageSource.getMessage(AppConstants.STATE_VALUE_ERROR, new Object[]{}, LocaleContextHolder.getLocale()));
    }

    public BigInteger getRideCost(){
        int intPart = RANDOM.nextInt(0,99);
        int fractPart = RANDOM.nextInt(0,99);
        return BigInteger.valueOf(intPart)
                .multiply(BigInteger.valueOf(100))
                .add(BigInteger.valueOf(fractPart));
    }
}
