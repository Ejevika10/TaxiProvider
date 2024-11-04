package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.exception.NotFoundException;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.PassengerRating;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.service.RabbitService;
import com.modsen.ratingservice.util.AppConstants;
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
class PassengerRatingServiceImplTest {

    @Mock
    private PassengerRatingRepository passengerRatingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private RatingListMapper ratingListMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private RabbitService rabbitService;

    @Mock
    private RatingCounterService ratingCounterService;

    @Mock
    private PassengerValidatorService validator;

    @InjectMocks
    private PassengerRatingServiceImpl passengerRatingService;

    private final PassengerRating passengerRating = new PassengerRating("1", 1L, 1L, 1, "cool", false);
    private final RatingRequestDto passengerRatingRequestDto = new RatingRequestDto(1L, 1L, 1, "cool");
    private final RatingResponseDto passengerRatingResponseDto = new RatingResponseDto("1", 1L, 1L, 1, "cool");

    private final RatingRequestDto passengerRatingNewRequestDto = new RatingRequestDto(1L, 1L, 3, "coolNew");

    private final List<PassengerRating> passengerRatingList = List.of(passengerRating);
    private final List<RatingResponseDto> passengerRatingResponseDtoList = List.of(passengerRatingResponseDto);

    @Test
    void getAllRatings() {
        //Arrange
        when(passengerRatingRepository.findAllByDeletedIsFalse()).thenReturn(passengerRatingList);
        when(ratingListMapper.toPassengerRatingResponseDtoList(passengerRatingList)).thenReturn(passengerRatingResponseDtoList);

        //Act
        List<RatingResponseDto> actual = passengerRatingService.getAllRatings();

        //Assert
        verify(passengerRatingRepository).findAllByDeletedIsFalse();
        verify(ratingListMapper).toPassengerRatingResponseDtoList(passengerRatingList);
        assertEquals(passengerRatingResponseDtoList.size(), actual.size());
        assertIterableEquals(passengerRatingResponseDtoList, actual);
    }

    @Test
    void getPageRatings() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RatingResponseDto> expected = new PageDto<>(offset, limit, 1, passengerRatingResponseDtoList.size(), passengerRatingResponseDtoList);
        Page<PassengerRating> ratingPage = new PageImpl<>(passengerRatingList, pageRequest, 1);
        when(passengerRatingRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = passengerRatingService.getPageRatings(offset, limit);

        //Assert
        verify(passengerRatingRepository).findAllByDeletedIsFalse(pageRequest);
        verify(ratingMapper, times(actual.content().size())).toRatingResponseDto(any(PassengerRating.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(actual.content(), expected.content());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(actual.pageSize(), expected.pageSize());
        assertEquals(actual.totalElements(), expected.totalElements());
        assertEquals(actual.totalPages(), expected.totalPages());
    }

    @Test
    void getAllRatingsByUserId() {
        //Arrange
        when(passengerRatingRepository.findAllByUserIdAndDeletedIsFalse(anyLong())).thenReturn(passengerRatingList);
        when(ratingListMapper.toPassengerRatingResponseDtoList(passengerRatingList)).thenReturn(passengerRatingResponseDtoList);

        //Act
        List<RatingResponseDto> actual = passengerRatingService.getAllRatingsByUserId(passengerRatingRequestDto.userId());

        //Assert
        verify(passengerRatingRepository).findAllByUserIdAndDeletedIsFalse(passengerRatingRequestDto.userId());
        verify(ratingListMapper).toPassengerRatingResponseDtoList(passengerRatingList);
        assertEquals(passengerRatingResponseDtoList.size(), actual.size());
        assertIterableEquals(passengerRatingResponseDtoList, actual);
    }

    @Test
    void getPageRatingsByUserId() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RatingResponseDto> expected = new PageDto<>(offset, limit, 1, passengerRatingResponseDtoList.size(), passengerRatingResponseDtoList);
        Page<PassengerRating> ratingPage = new PageImpl<>(passengerRatingList, pageRequest, 1);
        when(passengerRatingRepository.findAllByUserIdAndDeletedIsFalse(passengerRating.getUserId(), pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = passengerRatingService.getPageRatingsByUserId(passengerRating.getUserId(), offset, limit);

        //Assert
        verify(passengerRatingRepository).findAllByUserIdAndDeletedIsFalse(passengerRating.getUserId(), pageRequest);
        verify(ratingMapper, times(actual.content().size())).toRatingResponseDto(any(PassengerRating.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(expected.content(), actual.content());
        assertEquals(expected.pageNumber(), actual.pageNumber());
        assertEquals(expected.pageSize(), actual.pageSize());
        assertEquals(expected.totalElements(), actual.totalElements());
        assertEquals(expected.totalPages(), actual.totalPages());
    }

    @Test
    void getRatingById_ExistingId_ReturnsValidDto() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.getRatingById(passengerRating.getId());

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
        verify(ratingMapper).toRatingResponseDto(passengerRating);
        assertEquals(passengerRatingResponseDto, actual);
    }

    @Test
    void getRatingById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerRatingService.getRatingById(passengerRating.getId()),
                AppConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
    }

    @Test
    void addRating_UniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
        when(ratingMapper.toPassengerRating(any(RatingRequestDto.class))).thenReturn(passengerRating);
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.addRating(passengerRatingRequestDto);

        //Assert
        verify(validator).ratingExistsByRideId(passengerRating.getRideId());
        verify(validator).rideExistsAndUserIsCorrect(passengerRating.getRideId(), passengerRating.getUserId());
        verify(ratingMapper).toPassengerRating(passengerRatingRequestDto);
        verify(passengerRatingRepository).save(passengerRating);
        verify(ratingMapper).toRatingResponseDto(passengerRating);
        assertEquals(passengerRatingResponseDto, actual);
    }

    @Test
    void updateRating_ExistingRatingUniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.updateRating(passengerRating.getId(), passengerRatingNewRequestDto);

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
        verify(validator).rideExistsAndUserIsCorrect(passengerRatingNewRequestDto.rideId(), passengerRatingNewRequestDto.userId());
        verify(ratingMapper).updatePassengerRating(passengerRatingNewRequestDto, passengerRating);
        verify(passengerRatingRepository).save(passengerRating);
        verify(ratingMapper).toRatingResponseDto(passengerRating);
    }

    @Test
    void updateRating_NonExistingRating_ReturnsNotFoundException() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerRatingService.updateRating("1", passengerRatingRequestDto),
                AppConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse("1");
    }

    @Test
    void deleteRating_ExistingId() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);

        //Act
        passengerRatingService.deleteRating(passengerRating.getId());

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
        verify(passengerRatingRepository).save(passengerRating);
        assertTrue(passengerRating.getDeleted());
    }

    @Test
    void deleteRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerRatingService.deleteRating("1"),
                AppConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse("1");
    }
}