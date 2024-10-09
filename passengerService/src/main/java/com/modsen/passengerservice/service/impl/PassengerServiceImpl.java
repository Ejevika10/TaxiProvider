package com.modsen.passengerservice.service.impl;

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
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    private final PassengerMapper passengerMapper;

    private final PassengerListMapper passengerListMapper;

    private final PageMapper pageMapper;

    private final MessageSource messageSource;

    @Override
    public List<PassengerResponseDto> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAllByDeletedIsFalse();
        return passengerListMapper.toPassengerResponseDTOList(passengers);
    }

    @Override
    public PageDto<PassengerResponseDto> getPagePassengers(Integer offset, Integer limit) {
        Page<PassengerResponseDto> passengers = passengerRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit)).map(passengerMapper::toPassengerResponseDTO);
        return pageMapper.pageToDto(passengers);
    }

    @Override
    public PassengerResponseDto getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("passenger.notfound", new Object[]{}, Locale.US)));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    public PassengerResponseDto getPassengerByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("passenger.notfound", new Object[]{}, Locale.US)));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDto addPassenger(PassengerRequestDto requestDTO) {
        if(passengerRepository.existsByEmailAndDeletedIsFalse(requestDTO.email())) {
            throw new DuplicateFieldException(
                    messageSource.getMessage("passenger.email.exist", new Object[]{}, Locale.US));
        }
        Passenger passenger = passengerRepository.save(passengerMapper.toPassenger(requestDTO));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto requestDTO) {
        if(passengerRepository.existsByIdAndDeletedIsFalse(id)) {
            Optional<Passenger> existingPassenger = passengerRepository.findByEmailAndDeletedIsFalse(requestDTO.email());
            if(existingPassenger.isPresent() && !existingPassenger.get().getId().equals(id)) {
                throw new DuplicateFieldException(
                        messageSource.getMessage("passenger.email.exist", new Object[]{}, Locale.US));
            }
            Passenger passengerToSave = passengerRepository.save(passengerMapper.toPassenger(requestDTO));
            passengerToSave.setId(id);
            Passenger passenger = passengerRepository.save(passengerToSave);
            return passengerMapper.toPassengerResponseDTO(passenger);
        }
        throw new NotFoundException(messageSource.getMessage("passenger.notfound", new Object[]{}, Locale.US));
    }

    @Override
    @Transactional
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("passenger.notfound", new Object[]{}, Locale.US)));
        passenger.setDeleted(true);
        passengerRepository.save(passenger);
    }
}
