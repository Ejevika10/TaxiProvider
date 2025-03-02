package com.modsen.ratingservice.service.impl;

import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.PassengerRating;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.service.RabbitService;
import com.modsen.ratingservice.util.MessageConstants;
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

import static com.modsen.ratingservice.util.TestData.AUTHORIZATION_VALUE;
import static com.modsen.ratingservice.util.TestData.LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.NON_EXISTING_RATING_ID;
import static com.modsen.ratingservice.util.TestData.OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.RATING_ID;
import static com.modsen.ratingservice.util.TestData.USER_ID;
import static com.modsen.ratingservice.util.TestData.getPassengerRating;
import static com.modsen.ratingservice.util.TestData.getPassengerRatingList;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDto;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getAllRatings() {
        //Arrange
        List<PassengerRating> passengerRatingList = getPassengerRatingList();
        List<RatingResponseDto> passengerRatingResponseDtoList = getRatingResponseDtoList();
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
        List<PassengerRating> passengerRatingList = getPassengerRatingList();
        List<RatingResponseDto> passengerRatingResponseDtoList = getRatingResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RatingResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, passengerRatingResponseDtoList.size(), passengerRatingResponseDtoList);
        Page<PassengerRating> ratingPage = new PageImpl<>(passengerRatingList, pageRequest, 1);
        when(passengerRatingRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = passengerRatingService.getPageRatings(OFFSET_VALUE, LIMIT_VALUE);

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
        List<PassengerRating> passengerRatingList = getPassengerRatingList();
        List<RatingResponseDto> passengerRatingResponseDtoList = getRatingResponseDtoList();
        when(passengerRatingRepository.findAllByUserIdAndDeletedIsFalse(any(UUID.class))).thenReturn(passengerRatingList);
        when(ratingListMapper.toPassengerRatingResponseDtoList(passengerRatingList)).thenReturn(passengerRatingResponseDtoList);

        //Act
        List<RatingResponseDto> actual = passengerRatingService.getAllRatingsByUserId(USER_ID);

        //Assert
        verify(passengerRatingRepository).findAllByUserIdAndDeletedIsFalse(USER_ID);
        verify(ratingListMapper).toPassengerRatingResponseDtoList(passengerRatingList);
        assertEquals(passengerRatingResponseDtoList.size(), actual.size());
        assertIterableEquals(passengerRatingResponseDtoList, actual);
    }

    @Test
    void getPageRatingsByUserId() {
        //Arrange
        List<PassengerRating> passengerRatingList = getPassengerRatingList();
        List<RatingResponseDto> passengerRatingResponseDtoList = getRatingResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RatingResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, passengerRatingResponseDtoList.size(), passengerRatingResponseDtoList);
        Page<PassengerRating> ratingPage = new PageImpl<>(passengerRatingList, pageRequest, 1);
        when(passengerRatingRepository.findAllByUserIdAndDeletedIsFalse(USER_ID, pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = passengerRatingService.getPageRatingsByUserId(USER_ID, OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(passengerRatingRepository).findAllByUserIdAndDeletedIsFalse(USER_ID, pageRequest);
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
        PassengerRating passengerRating = getPassengerRating();
        RatingResponseDto passengerRatingResponseDto = getRatingResponseDto();
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.getRatingById(RATING_ID);

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(RATING_ID);
        verify(ratingMapper).toRatingResponseDto(passengerRating);
        assertEquals(passengerRatingResponseDto, actual);
    }

    @Test
    void getRatingById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        PassengerRating passengerRating = getPassengerRating();
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerRatingService.getRatingById(passengerRating.getId()),
                MessageConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
    }

    @Test
    void addRating_UniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
        PassengerRating passengerRating = getPassengerRating();
        RatingRequestDto passengerRatingRequestDto = getRatingRequestDto();
        RatingResponseDto passengerRatingResponseDto = getRatingResponseDto();
        when(ratingMapper.toPassengerRating(any(RatingRequestDto.class))).thenReturn(passengerRating);
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.addRating(passengerRatingRequestDto, AUTHORIZATION_VALUE);

        //Assert
        verify(validator).validateForCreate(passengerRatingRequestDto, AUTHORIZATION_VALUE);
        verify(ratingMapper).toPassengerRating(passengerRatingRequestDto);
        verify(passengerRatingRepository).save(passengerRating);
        verify(ratingMapper).toRatingResponseDto(passengerRating);
        assertEquals(passengerRatingResponseDto, actual);
    }

    @Test
    void updateRating_ExistingRatingUniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
        PassengerRating passengerRating = getPassengerRating();
        RatingRequestDto passengerRatingRequestDto = getRatingRequestDto();
        RatingResponseDto passengerRatingResponseDto = getRatingResponseDto();
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);
        when(ratingMapper.toRatingResponseDto(passengerRating)).thenReturn(passengerRatingResponseDto);

        //Act
        RatingResponseDto actual = passengerRatingService.updateRating(passengerRating.getId(), passengerRatingRequestDto, AUTHORIZATION_VALUE);

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(passengerRating.getId());
        verify(validator).validateForUpdate(passengerRatingRequestDto, AUTHORIZATION_VALUE);
        verify(ratingMapper).updatePassengerRating(passengerRatingRequestDto, passengerRating);
        verify(passengerRatingRepository).save(passengerRating);
        verify(ratingMapper).toRatingResponseDto(passengerRating);
    }

    @Test
    void updateRating_NonExistingRating_ReturnsNotFoundException() {
        //Arrange
        RatingRequestDto passengerRatingRequestDto = getRatingRequestDto();
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> passengerRatingService.updateRating(NON_EXISTING_RATING_ID, passengerRatingRequestDto, AUTHORIZATION_VALUE),
                MessageConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(NON_EXISTING_RATING_ID);
    }

    @Test
    void deleteRating_ExistingId() {
        //Arrange
        PassengerRating passengerRating = getPassengerRating();
        when(passengerRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(passengerRating));
        when(passengerRatingRepository.save(passengerRating)).thenReturn(passengerRating);

        //Act
        passengerRatingService.deleteRating(passengerRating.getId());

        //Assert
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(RATING_ID);
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
                () -> passengerRatingService.deleteRating(NON_EXISTING_RATING_ID),
                MessageConstants.RATING_NOT_FOUND);
        verify(passengerRatingRepository).findByIdAndDeletedIsFalse(NON_EXISTING_RATING_ID);
    }
}