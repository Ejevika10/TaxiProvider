package com.modsen.passengerservice.service.impl;

import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.mapper.PageMapper;
import com.modsen.passengerservice.mapper.PassengerListMapper;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.model.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.RabbitService;
import com.modsen.passengerservice.util.MessageConstants;
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

import static com.modsen.passengerservice.util.TestData.LIMIT_VALUE;
import static com.modsen.passengerservice.util.TestData.NEW_RATING;
import static com.modsen.passengerservice.util.TestData.OFFSET_VALUE;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID;
import static com.modsen.passengerservice.util.TestData.PASSENGER_ID_2;
import static com.modsen.passengerservice.util.TestData.getPassenger;
import static com.modsen.passengerservice.util.TestData.getPassengerBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerCreateRequestDto;
import static com.modsen.passengerservice.util.TestData.getPassengerList;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDto;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDtoBuilder;
import static com.modsen.passengerservice.util.TestData.getPassengerResponseDtoList;
import static com.modsen.passengerservice.util.TestData.getPassengerUpdateRequestDto;
import static com.modsen.passengerservice.util.TestData.getUserRatingDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private PassengerListMapper passengerListMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private RabbitService rabbitService;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void getAllPassengers() {
        //Arrange
        List<Passenger> passengerList = getPassengerList();
        List<PassengerResponseDto> passengerResponseDtoList = getPassengerResponseDtoList();
        when(passengerRepository.findAllByDeletedIsFalse()).thenReturn(passengerList);
        when(passengerListMapper.toPassengerResponseDTOList(passengerList)).thenReturn(passengerResponseDtoList);

        //Act
        List<PassengerResponseDto> actual = passengerService.getAllPassengers();

        //Assert
        verify(passengerRepository).findAllByDeletedIsFalse();
        verify(passengerListMapper).toPassengerResponseDTOList(passengerList);
        assertEquals(actual.size(), passengerResponseDtoList.size());
        assertIterableEquals(actual, passengerResponseDtoList);
    }

    @Test
    void getPagePassengers() {
        //Arrange
        List<Passenger> passengerList = getPassengerList();
        List<PassengerResponseDto> passengerResponseDtoList = getPassengerResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<PassengerResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, passengerResponseDtoList.size(), passengerResponseDtoList);
        Page<Passenger> passengerPage = new PageImpl<>(passengerList, pageRequest, 1);
        when(passengerRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(passengerPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<PassengerResponseDto> actual = passengerService.getPagePassengers(OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(passengerRepository).findAllByDeletedIsFalse(pageRequest);
        verify(passengerMapper, times(actual.content().size())).toPassengerResponseDTO(any(Passenger.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(actual.content(), expected.content());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(actual.pageSize(), expected.pageSize());
        assertEquals(actual.totalElements(), expected.totalElements());
        assertEquals(actual.totalPages(), expected.totalPages());
    }

    @Test
    void getPassengerById_ExistingId_ReturnsValidDto() {
        //Arrange
        Passenger passenger = getPassenger();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(passenger));
        when(passengerMapper.toPassengerResponseDTO(passenger)).thenReturn(passengerResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.getPassengerById(passenger.getId());

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passenger.getId());
        verify(passengerMapper).toPassengerResponseDTO(passenger);
        assertEquals(actual, passengerResponseDto);
    }

    @Test
    void getPassengerById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.getPassengerById(PASSENGER_ID),
                MessageConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(PASSENGER_ID);
    }

    @Test
    void getPassengerByEmail_ExistingEmail_ReturnsValidDto() {
        //Arrange
        Passenger passenger = getPassenger();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passenger));
        when(passengerMapper.toPassengerResponseDTO(passenger)).thenReturn(passengerResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.getPassengerByEmail(passenger.getEmail());

        //Assert
        assertEquals(actual, passengerResponseDto);
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passenger.getEmail());
        verify(passengerMapper).toPassengerResponseDTO(passenger);
    }

    @Test
    void getPassengerByEmail_NonExistingEmail_ReturnsNotFoundException() {
        //Arrange
        Passenger passenger = getPassenger();
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.getPassengerByEmail(passenger.getEmail()),
                MessageConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passenger.getEmail());
    }

    @Test
    void addPassenger_UniqueEmail_ReturnsValidDto() {
        //Arrange
        Passenger passenger = getPassenger();
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDto();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        when(passengerRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);
        when(passengerMapper.toPassenger(passengerRequestDto)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDTO(passenger)).thenReturn(passengerResponseDto);
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        //Act
        PassengerResponseDto actual = passengerService.addPassenger(passengerRequestDto);

        //Assert
        verify(passengerRepository).existsByEmailAndDeletedIsFalse(passengerRequestDto.email());
        verify(passengerMapper).toPassenger(passengerRequestDto);
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).toPassengerResponseDTO(passenger);
        assertEquals(actual, passengerResponseDto);
    }

    @Test
    void addPassenger_NonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        PassengerCreateRequestDto passengerRequestDto = getPassengerCreateRequestDto();
        when(passengerRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> passengerService.addPassenger(passengerRequestDto),
                MessageConstants.PASSENGER_EMAIL_EXISTS);
        verify(passengerRepository).existsByEmailAndDeletedIsFalse(passengerRequestDto.email());
    }

    @Test
    void updatePassenger_ExistingIdUniqueEmail_ReturnsValidDto() {
        //Arrange
        Passenger passenger = getPassenger();
        PassengerResponseDto passengerResponseDto = getPassengerResponseDto();
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(passenger));
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDTO(passenger)).thenReturn(passengerResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.updatePassenger(passenger.getId(), passengerRequestDto);

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passenger.getId());
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerRequestDto.email());
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).toPassengerResponseDTO(passenger);
        assertEquals(actual, passengerResponseDto);
    }

    @Test
    void updatePassenger_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.updatePassenger(PASSENGER_ID, passengerRequestDto),
                MessageConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(PASSENGER_ID);
    }

    @Test
    void updatePassenger_ExistingIdNonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        PassengerUpdateRequestDto passengerRequestDto = getPassengerUpdateRequestDto();
        Passenger passenger = getPassenger();
        Passenger passengerWithSameEmail = getPassengerBuilder()
                .id(PASSENGER_ID_2)
                .build();

        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(passenger));
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerWithSameEmail));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> passengerService.updatePassenger(PASSENGER_ID, passengerRequestDto),
                MessageConstants.PASSENGER_EMAIL_EXISTS);
        verify(passengerRepository).findByIdAndDeletedIsFalse(PASSENGER_ID);
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerRequestDto.email());
    }

    @Test
    void deletePassenger_ExistingId() {
        //Arrange
        Passenger passenger = getPassenger();
        when(passengerRepository.findByIdAndDeletedIsFalse(passenger.getId())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        //Act
        passengerService.deletePassenger(passenger.getId());

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passenger.getId());
        verify(passengerRepository).save(passenger);
        assertTrue(passenger.getDeleted());
    }

    @Test
    void deletePassenger_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.deletePassenger(PASSENGER_ID),
                MessageConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(PASSENGER_ID);
    }

    @Test
    void updateRating_ExistingId_ReturnsValidDto() {
        //Arrange
        Passenger passenger = getPassenger();
        UserRatingDto passengerRating = getUserRatingDto();
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        PassengerResponseDto passengerUpdatedResponseDto = getPassengerResponseDtoBuilder()
                .rating(NEW_RATING)
                .build();
        when(passengerMapper.toPassengerResponseDTO(passenger)).thenReturn(passengerUpdatedResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.updateRating(passengerRating);

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerRating.id());
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).toPassengerResponseDTO(passenger);
        assertEquals(passengerRating.rating(), actual.rating());
    }

    @Test
    void updateRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        UserRatingDto passengerRating = getUserRatingDto();
        when(passengerRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.updateRating(passengerRating),
                MessageConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerRating.id());
    }
}