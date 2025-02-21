package com.modsen.passengerservice.service.impl;

import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.dto.UserDeleteRequestDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.dto.UserUpdateRequestDto;
import com.modsen.passengerservice.service.RabbitService;
import com.modsen.passengerservice.util.AppConstants;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.mapper.PageMapper;
import com.modsen.passengerservice.mapper.PassengerListMapper;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.model.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    private final RabbitService rabbitService;

    private final PassengerMapper passengerMapper;

    private final PassengerListMapper passengerListMapper;

    private final PageMapper pageMapper;

    @Override
    public List<PassengerResponseDto> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAllByDeletedIsFalse();
        return passengerListMapper.toPassengerResponseDTOList(passengers);
    }

    @Override
    public PageDto<PassengerResponseDto> getPagePassengers(Integer offset, Integer limit) {
        Page<PassengerResponseDto> passengers = passengerRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit))
                .map(passengerMapper::toPassengerResponseDTO);
        return pageMapper.pageToDto(passengers);
    }

    @Override
    @Cacheable(value = "passenger", key = "#id")
    public PassengerResponseDto getPassengerById(UUID id) {
        Passenger passenger = findByIdOrThrow(id);
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Cacheable(value = "passenger", key = "#result.id()")
    public PassengerResponseDto getPassengerByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    @CachePut(value = "passenger", key = "#result.id()")
    public PassengerResponseDto addPassenger(PassengerCreateRequestDto requestDTO) {
        if(passengerRepository.existsByEmailAndDeletedIsFalse(requestDTO.email())) {
            throw new DuplicateFieldException(AppConstants.PASSENGER_EMAIL_EXISTS);
        }
        Passenger passengerToSave = passengerMapper.toPassenger(requestDTO);
        Passenger passenger = passengerRepository.save(passengerToSave);
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    @CachePut(value = "passenger", key = "#id")
    public PassengerResponseDto updatePassenger(UUID id, PassengerUpdateRequestDto requestDTO) {
        Passenger passengerToSave = findByIdOrThrow(id);
        Optional<Passenger> existingPassenger = passengerRepository.findByEmailAndDeletedIsFalse(requestDTO.email());
        if(existingPassenger.isPresent() && !existingPassenger.get().getId().equals(id)) {
            throw new DuplicateFieldException(AppConstants.PASSENGER_EMAIL_EXISTS);
        }
        passengerMapper.updatePassenger(passengerToSave, requestDTO);
        Passenger passenger = passengerRepository.save(passengerToSave);

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(passenger.getId().toString(),
                passenger.getName(),
                passenger.getEmail(),
                passenger.getPhone());
        rabbitService.sendUpdateMessage(EXCHANGE_NAME, UPDATE_ROUTING_KEY, userUpdateRequestDto);

        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    @CacheEvict(value = "passenger", key = "#id")
    public void deletePassenger(UUID id) {
        Passenger passenger = findByIdOrThrow(id);
        passenger.setDeleted(true);
        passengerRepository.save(passenger);

        UserDeleteRequestDto userDeleteRequestDto = new UserDeleteRequestDto(id.toString());
        rabbitService.sendDeleteMessage(EXCHANGE_NAME, DELETE_ROUTING_KEY, userDeleteRequestDto);
    }

    @Override
    @Transactional
    @CachePut(value = "passenger", key = "#result.id()")
    public PassengerResponseDto updateRating(UserRatingDto userRatingDTO) {
        Passenger passengerToSave = findByIdOrThrow(userRatingDTO.id());
        passengerToSave.setRating(userRatingDTO.rating());
        Passenger passenger = passengerRepository.save(passengerToSave);
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    private Passenger findByIdOrThrow(UUID id) {
        return passengerRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
    }
}
