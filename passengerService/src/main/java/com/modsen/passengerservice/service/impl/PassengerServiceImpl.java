package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.util.AppConstants;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.exception.DuplicateFieldException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.mapper.PageMapper;
import com.modsen.passengerservice.mapper.PassengerListMapper;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.model.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

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
    public PassengerResponseDto getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    public PassengerResponseDto getPassengerByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDto addPassenger(PassengerRequestDto requestDTO) {
        if(passengerRepository.existsByEmailAndDeletedIsFalse(requestDTO.email())) {
            throw new DuplicateFieldException(AppConstants.PASSENGER_EMAIL_EXISTS);
        }
        Passenger passengerToSave = passengerMapper.toPassenger(requestDTO);
        Passenger passenger = passengerRepository.save(passengerToSave);
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto requestDTO) {
        if(passengerRepository.existsByIdAndDeletedIsFalse(id)) {
            Optional<Passenger> existingPassenger = passengerRepository.findByEmailAndDeletedIsFalse(requestDTO.email());
            if(existingPassenger.isPresent() && !existingPassenger.get().getId().equals(id)) {
                throw new DuplicateFieldException(AppConstants.PASSENGER_EMAIL_EXISTS);
            }
            Passenger passengerToSave = passengerMapper.toPassenger(requestDTO);
            passengerToSave.setId(id);
            Passenger passenger = passengerRepository.save(passengerToSave);
            return passengerMapper.toPassengerResponseDTO(passenger);
        }
        throw new NotFoundException(AppConstants.PASSENGER_NOT_FOUND);
    }

    @Override
    @Transactional
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
        passenger.setDeleted(true);
        passengerRepository.save(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDto updateRating(UserRatingDto userRatingDTO) {
        Passenger passengerToSave = passengerRepository.findById(userRatingDTO.id())
                .orElseThrow(() -> new NotFoundException(AppConstants.PASSENGER_NOT_FOUND));
        passengerToSave.setRating(userRatingDTO.rating());
        Passenger passenger = passengerRepository.save(passengerToSave);
        return passengerMapper.toPassengerResponseDTO(passenger);
    }
}
