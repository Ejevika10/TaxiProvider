package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.PageDTO;
import com.modsen.passengerservice.dto.PassengerRequestDTO;
import com.modsen.passengerservice.dto.PassengerResponseDTO;
import com.modsen.passengerservice.exception.DuplicateFieldException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.mapper.PageMapper;
import com.modsen.passengerservice.mapper.PassengerListMapper;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.model.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
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
    public List<PassengerResponseDTO> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        return passengerListMapper.toPassengerResponseDTOList(passengers);
    }

    @Override
    public PageDTO<PassengerResponseDTO> getPagePassengers(Integer offset, Integer limit) {
        Page<PassengerResponseDTO> passengers = passengerRepository.findAll(PageRequest.of(offset, limit)).map(passengerMapper::toPassengerResponseDTO);
        return pageMapper.pageToDto(passengers);
    }

    @Override
    public PassengerResponseDTO getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Passenger not found", 404L));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    public PassengerResponseDTO getPassengerByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmailAndDeletedIsFalse(email).orElseThrow(() -> new NotFoundException("Passenger not found", 404L));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDTO addPassenger(PassengerRequestDTO requestDTO) {
        if(passengerRepository.existsByEmailAndDeletedIsFalse(requestDTO.getEmail())) {
            throw new DuplicateFieldException("Passenger with this email already exist", 400L);
        }
        Passenger passenger = passengerRepository.save(passengerMapper.toPassenger(requestDTO));
        return passengerMapper.toPassengerResponseDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerResponseDTO updatePassenger(PassengerRequestDTO requestDTO) {
        if(passengerRepository.existsByIdAndDeletedIsFalse(requestDTO.getId())) {
            Optional<Passenger> existingPassenger = passengerRepository.findByEmailAndDeletedIsFalse(requestDTO.getEmail());
            if(existingPassenger.isPresent() && !existingPassenger.get().getId().equals(requestDTO.getId())) {
                throw new DuplicateFieldException("Passenger with this email already exist", 400L);
            }
            Passenger passenger = passengerRepository.save(passengerMapper.toPassenger(requestDTO));
            return passengerMapper.toPassengerResponseDTO(passenger);
        }
        throw new NotFoundException("Passenger not found", 404L);
    }

    @Override
    @Transactional
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id).orElseThrow(() -> new NotFoundException("Passenger not found", 404L));
        passenger.setDeleted(true);
        passengerRepository.save(passenger);
    }
}
