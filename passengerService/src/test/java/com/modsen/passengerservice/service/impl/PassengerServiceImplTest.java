package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.exception.DuplicateFieldException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.mapper.PageMapper;
import com.modsen.passengerservice.mapper.PassengerListMapper;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.model.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.util.AppConstants;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private final UserRatingDto passengerRating = new UserRatingDto(1L, 5.0);

    private final Passenger passengerA = new Passenger(1L, "PassengerA", "passengerA@mail.ru", "712345678", 0.0, false);
    private final PassengerRequestDto passengerARequestDto = new PassengerRequestDto("PassengerA", "passengerA@mail.ru", "712345678", 0.0);
    private final PassengerResponseDto passengerAResponseDto = new PassengerResponseDto(1L, "PassengerA", "passengerA@mail.ru", "712345678", 0.0);

    private final Passenger passengerB = new Passenger(2L, "PassengerB", "passengerB@mail.ru", "712345678", 0.0, false);
    private final PassengerRequestDto passengerBRequestDto = new PassengerRequestDto("PassengerB", "passengerB@mail.ru", "712345678", 0.0);
    private final PassengerResponseDto passengerBResponseDto = new PassengerResponseDto(2L, "PassengerB", "passengerB@mail.ru", "712345678", 0.0);

    private final List<Passenger> passengerList = List.of(passengerA, passengerB);
    private final List<PassengerResponseDto> passengerResponseDtoList = List.of(passengerAResponseDto, passengerBResponseDto);

    @Test
    void getAllPassengers() {
        //Arrange
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
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<PassengerResponseDto> expected = new PageDto<>(offset, limit, 1, passengerResponseDtoList.size(), passengerResponseDtoList);
        Page<Passenger> passengerPage = new PageImpl<>(passengerList, pageRequest, 1);
        when(passengerRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(passengerPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<PassengerResponseDto> actual = passengerService.getPagePassengers(offset, limit);

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
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(passengerA));
        when(passengerMapper.toPassengerResponseDTO(passengerA)).thenReturn(passengerAResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.getPassengerById(passengerA.getId());

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerA.getId());
        verify(passengerMapper).toPassengerResponseDTO(passengerA);
        assertEquals(actual, passengerAResponseDto);
    }

    @Test
    void getPassengerById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.getPassengerById(3L),
                AppConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void getPassengerByEmail_ExistingEmail_ReturnsValidDto() {
        //Arrange
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerA));
        when(passengerMapper.toPassengerResponseDTO(passengerA)).thenReturn(passengerAResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.getPassengerByEmail(passengerA.getEmail());

        //Assert
        assertEquals(actual, passengerAResponseDto);
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerA.getEmail());
        verify(passengerMapper).toPassengerResponseDTO(passengerA);
    }

    @Test
    void getPassengerByEmail_NonExistingEmail_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.getPassengerByEmail(passengerA.getEmail()),
                AppConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerA.getEmail());
    }

    @Test
    void addPassenger_UniqueEmail_ReturnsValidDto() {
        //Arrange
        when(passengerRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);
        when(passengerMapper.toPassenger(passengerARequestDto)).thenReturn(passengerA);
        when(passengerMapper.toPassengerResponseDTO(passengerA)).thenReturn(passengerAResponseDto);
        when(passengerRepository.save(passengerA)).thenReturn(passengerA);

        //Act
        PassengerResponseDto actual = passengerService.addPassenger(passengerARequestDto);

        //Assert
        verify(passengerRepository).existsByEmailAndDeletedIsFalse(passengerARequestDto.email());
        verify(passengerMapper).toPassenger(passengerARequestDto);
        verify(passengerRepository).save(passengerA);
        verify(passengerMapper).toPassengerResponseDTO(passengerA);
        assertEquals(actual, passengerAResponseDto);
    }

    @Test
    void addPassenger_NonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        when(passengerRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> passengerService.addPassenger(passengerARequestDto),
                AppConstants.PASSENGER_EMAIL_EXISTS);
        verify(passengerRepository).existsByEmailAndDeletedIsFalse(passengerARequestDto.email());
    }

    @Test
    void updatePassenger_ExistingIdUniqueEmail_ReturnsValidDto() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(passengerA));
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(passengerRepository.save(passengerA)).thenReturn(passengerA);
        when(passengerMapper.toPassengerResponseDTO(passengerA)).thenReturn(passengerAResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.updatePassenger(passengerA.getId(), passengerARequestDto);

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerA.getId());
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerARequestDto.email());
        verify(passengerRepository).save(passengerA);
        verify(passengerMapper).toPassengerResponseDTO(passengerA);
        assertEquals(actual, passengerAResponseDto);
    }

    @Test
    void updatePassenger_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.updatePassenger(3L, passengerARequestDto),
                AppConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void updatePassenger_ExistingIdNonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(passengerA));
        when(passengerRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerB));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> passengerService.updatePassenger(passengerA.getId(), passengerBRequestDto),
                AppConstants.PASSENGER_EMAIL_EXISTS);
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerA.getId());
        verify(passengerRepository).findByEmailAndDeletedIsFalse(passengerBRequestDto.email());
    }

    @Test
    void deletePassenger_ExistingId() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(passengerA.getId())).thenReturn(Optional.of(passengerA));
        when(passengerRepository.save(passengerA)).thenReturn(passengerA);

        //Act
        passengerService.deletePassenger(passengerA.getId());

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerA.getId());
        verify(passengerRepository).save(passengerA);
        assertTrue(passengerA.getDeleted());
    }

    @Test
    void deletePassenger_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.deletePassenger(3L),
                AppConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void updateRating_ExistingId_ReturnsValidDto() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(passengerA));
        when(passengerRepository.save(passengerA)).thenReturn(passengerA);
        PassengerResponseDto passengerAUpdatedResponseDto = new PassengerResponseDto(1L, "PassengerA", "passengerA@mail.ru", "712345678", 5.0);
        when(passengerMapper.toPassengerResponseDTO(passengerA)).thenReturn(passengerAUpdatedResponseDto);

        //Act
        PassengerResponseDto actual = passengerService.updateRating(passengerRating);

        //Assert
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerRating.id());
        verify(passengerRepository).save(passengerA);
        verify(passengerMapper).toPassengerResponseDTO(passengerA);
        assertEquals(passengerRating.rating(), actual.rating());
    }

    @Test
    void updateRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerService.updateRating(passengerRating),
                AppConstants.PASSENGER_NOT_FOUND);
        verify(passengerRepository).findByIdAndDeletedIsFalse(passengerRating.id());
    }
}