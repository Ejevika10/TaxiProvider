package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.exception.NotFoundException;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.repository.DriverRatingRepository;
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

import static com.modsen.ratingservice.util.TestData.LIMIT_VALUE;
import static com.modsen.ratingservice.util.TestData.NON_EXISTING_RATING_ID;
import static com.modsen.ratingservice.util.TestData.OFFSET_VALUE;
import static com.modsen.ratingservice.util.TestData.RATING_ID;
import static com.modsen.ratingservice.util.TestData.USER_ID;
import static com.modsen.ratingservice.util.TestData.getDriverRating;
import static com.modsen.ratingservice.util.TestData.getDriverRatingList;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDtoBuilder;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDto;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoList;
import static com.modsen.ratingservice.util.TestData.NEW_COMMENT;
import static com.modsen.ratingservice.util.TestData.NEW_RATING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class DriverRatingServiceImplTest {

    @Mock
    private DriverRatingRepository driverRatingRepository;

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
    private DriverValidatorService validator;

    @InjectMocks
    private DriverRatingServiceImpl driverRatingService;

    @Test
    void getAllRatings() {
        //Arrange
        List<DriverRating> driverRatingList = getDriverRatingList();
        List<RatingResponseDto> driverRatingResponseDtoList = getRatingResponseDtoList();
        when(driverRatingRepository.findAllByDeletedIsFalse()).thenReturn(driverRatingList);
        when(ratingListMapper.toDriverRatingResponseDtoList(driverRatingList)).thenReturn(driverRatingResponseDtoList);

        //Act
        List<RatingResponseDto> actual = driverRatingService.getAllRatings();

        //Assert
        verify(driverRatingRepository).findAllByDeletedIsFalse();
        verify(ratingListMapper).toDriverRatingResponseDtoList(driverRatingList);
        assertEquals(driverRatingResponseDtoList.size(), actual.size());
        assertIterableEquals(driverRatingResponseDtoList, actual);
    }

    @Test
    void getPageRatings() {
        //Arrange
        List<DriverRating> driverRatingList = getDriverRatingList();
        List<RatingResponseDto> driverRatingResponseDtoList = getRatingResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RatingResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, driverRatingResponseDtoList.size(), driverRatingResponseDtoList);
        Page<DriverRating> ratingPage = new PageImpl<>(driverRatingList, pageRequest, 1);
        when(driverRatingRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = driverRatingService.getPageRatings(OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(driverRatingRepository).findAllByDeletedIsFalse(pageRequest);
        verify(ratingMapper, times(actual.content().size())).toRatingResponseDto(any(DriverRating.class));
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
        RatingRequestDto driverRatingRequestDto = getRatingRequestDto();
        List<DriverRating> driverRatingList = getDriverRatingList();
        List<RatingResponseDto> driverRatingResponseDtoList = getRatingResponseDtoList();
        when(driverRatingRepository.findAllByUserIdAndDeletedIsFalse(anyLong())).thenReturn(driverRatingList);
        when(ratingListMapper.toDriverRatingResponseDtoList(driverRatingList)).thenReturn(driverRatingResponseDtoList);

        //Act
        List<RatingResponseDto> actual = driverRatingService.getAllRatingsByUserId(driverRatingRequestDto.userId());

        //Assert
        verify(driverRatingRepository).findAllByUserIdAndDeletedIsFalse(driverRatingRequestDto.userId());
        verify(ratingListMapper).toDriverRatingResponseDtoList(driverRatingList);
        assertEquals(driverRatingResponseDtoList.size(), actual.size());
        assertIterableEquals(driverRatingResponseDtoList, actual);
    }

    @Test
    void getPageRatingsByUserId() {
        //Arrange
        List<DriverRating> driverRatingList = getDriverRatingList();
        List<RatingResponseDto> driverRatingResponseDtoList = getRatingResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<RatingResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, driverRatingResponseDtoList.size(), driverRatingResponseDtoList);
        Page<DriverRating> ratingPage = new PageImpl<>(driverRatingList, pageRequest, 1);
        when(driverRatingRepository.findAllByUserIdAndDeletedIsFalse(USER_ID, pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = driverRatingService.getPageRatingsByUserId(USER_ID, OFFSET_VALUE, LIMIT_VALUE);

        //Assert
        verify(driverRatingRepository).findAllByUserIdAndDeletedIsFalse(USER_ID, pageRequest);
        verify(ratingMapper, times(actual.content().size())).toRatingResponseDto(any(DriverRating.class));
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
        DriverRating driverRating = getDriverRating();
        RatingResponseDto driverRatingResponseDto = getRatingResponseDto();
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverRating));
        when(ratingMapper.toRatingResponseDto(driverRating)).thenReturn(driverRatingResponseDto);

        //Act
        RatingResponseDto actual = driverRatingService.getRatingById(driverRating.getId());

        //Assert
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(driverRating.getId());
        verify(ratingMapper).toRatingResponseDto(driverRating);
        assertEquals(driverRatingResponseDto, actual);
    }

    @Test
    void getRatingById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverRatingService.getRatingById(RATING_ID),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(RATING_ID);
    }

    @Test
    void addRating_UniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
        DriverRating driverRating = getDriverRating();
        RatingResponseDto driverRatingResponseDto = getRatingResponseDto();
        RatingRequestDto driverRatingRequestDto = getRatingRequestDto();
        when(ratingMapper.toDriverRating(any(RatingRequestDto.class))).thenReturn(driverRating);
        when(driverRatingRepository.save(driverRating)).thenReturn(driverRating);
        when(ratingMapper.toRatingResponseDto(driverRating)).thenReturn(driverRatingResponseDto);

        //Act
        RatingResponseDto actual = driverRatingService.addRating(driverRatingRequestDto);

        //Assert
        verify(validator).ratingExistsByRideId(driverRating.getRideId());
        verify(validator).rideExistsAndUserIsCorrect(driverRating.getRideId(), driverRating.getUserId());
        verify(ratingMapper).toDriverRating(driverRatingRequestDto);
        verify(driverRatingRepository).save(driverRating);
        verify(ratingMapper).toRatingResponseDto(driverRating);
        assertEquals(driverRatingResponseDto, actual);
    }

    @Test
    void updateRating_ExistingRatingUniqueRideIdExistingRideIdCorrectDriverId_ReturnsValidDto() {
        //Arrange
        DriverRating driverRating = getDriverRating();
        RatingResponseDto driverRatingResponseDto = getRatingResponseDto();
        RatingRequestDto driverRatingRequestDto = getRatingRequestDtoBuilder()
                .rating(NEW_RATING)
                .comment(NEW_COMMENT)
                .build();
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverRating));
        when(driverRatingRepository.save(driverRating)).thenReturn(driverRating);
        when(ratingMapper.toRatingResponseDto(driverRating)).thenReturn(driverRatingResponseDto);

        //Act
        RatingResponseDto actual = driverRatingService.updateRating(driverRating.getId(), driverRatingRequestDto);

        //Assert
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(driverRating.getId());
        verify(validator).rideExistsAndUserIsCorrect(driverRatingRequestDto.rideId(), driverRatingRequestDto.userId());
        verify(ratingMapper).updateDriverRating(driverRatingRequestDto, driverRating);
        verify(driverRatingRepository).save(driverRating);
        verify(ratingMapper).toRatingResponseDto(driverRating);
    }

    @Test
    void updateRating_NonExistingRating_ReturnsNotFoundException() {
        //Arrange
        RatingRequestDto driverRatingRequestDto = getRatingRequestDto();
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverRatingService.updateRating(NON_EXISTING_RATING_ID, driverRatingRequestDto),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(NON_EXISTING_RATING_ID);
    }

    @Test
    void deleteRating_ExistingId() {
        //Arrange
        DriverRating driverRating = getDriverRating();
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverRating));
        when(driverRatingRepository.save(driverRating)).thenReturn(driverRating);

        //Act
        driverRatingService.deleteRating(driverRating.getId());

        //Assert
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(driverRating.getId());
        verify(driverRatingRepository).save(driverRating);
        assertTrue(driverRating.getDeleted());
    }

    @Test
    void deleteRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverRatingService.deleteRating(NON_EXISTING_RATING_ID),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(NON_EXISTING_RATING_ID);
    }
}