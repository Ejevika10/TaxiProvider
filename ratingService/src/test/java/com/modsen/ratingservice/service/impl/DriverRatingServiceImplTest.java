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

    private final DriverRating driverRating = new DriverRating("1", 1L, 1L, 1, "cool", false);
    private final RatingRequestDto driverRatingRequestDto = new RatingRequestDto(1L, 1L, 1, "cool");
    private final RatingResponseDto driverRatingResponseDto = new RatingResponseDto("1", 1L, 1L, 1, "cool");

    private final RatingRequestDto driverRatingNewRequestDto = new RatingRequestDto(1L, 1L, 3, "coolNew");

    private final List<DriverRating> driverRatingList = List.of(driverRating);
    private final List<RatingResponseDto> driverRatingResponseDtoList = List.of(driverRatingResponseDto);

    @Test
    void getAllRatings() {
        //Arrange
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
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RatingResponseDto> expected = new PageDto<>(offset, limit, 1, driverRatingResponseDtoList.size(), driverRatingResponseDtoList);
        Page<DriverRating> ratingPage = new PageImpl<>(driverRatingList, pageRequest, 1);
        when(driverRatingRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = driverRatingService.getPageRatings(offset, limit);

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
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<RatingResponseDto> expected = new PageDto<>(offset, limit, 1, driverRatingResponseDtoList.size(), driverRatingResponseDtoList);
        Page<DriverRating> ratingPage = new PageImpl<>(driverRatingList, pageRequest, 1);
        when(driverRatingRepository.findAllByUserIdAndDeletedIsFalse(driverRating.getUserId(), pageRequest)).thenReturn(ratingPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<RatingResponseDto> actual = driverRatingService.getPageRatingsByUserId(driverRating.getUserId(), offset, limit);

        //Assert
        verify(driverRatingRepository).findAllByUserIdAndDeletedIsFalse(driverRating.getUserId(), pageRequest);
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
                () -> driverRatingService.getRatingById(driverRating.getId()),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(driverRating.getId());
    }

    @Test
    void addRating_UniqueRideIdExistingRideIdCorrectPassengerId_ReturnsValidDto() {
        //Arrange
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
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverRating));
        when(driverRatingRepository.save(driverRating)).thenReturn(driverRating);
        when(ratingMapper.toRatingResponseDto(driverRating)).thenReturn(driverRatingResponseDto);

        //Act
        RatingResponseDto actual = driverRatingService.updateRating(driverRating.getId(), driverRatingNewRequestDto);

        //Assert
        verify(driverRatingRepository).findByIdAndDeletedIsFalse(driverRating.getId());
        verify(validator).rideExistsAndUserIsCorrect(driverRatingNewRequestDto.rideId(), driverRatingNewRequestDto.userId());
        verify(ratingMapper).updateDriverRating(driverRatingNewRequestDto, driverRating);
        verify(driverRatingRepository).save(driverRating);
        verify(ratingMapper).toRatingResponseDto(driverRating);
    }

    @Test
    void updateRating_NonExistingRating_ReturnsNotFoundException() {
        //Arrange
        when(driverRatingRepository.findByIdAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverRatingService.updateRating("1", driverRatingRequestDto),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse("1");
    }

    @Test
    void deleteRating_ExistingId() {
        //Arrange
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
                () -> driverRatingService.deleteRating("1"),
                AppConstants.RATING_NOT_FOUND);
        verify(driverRatingRepository).findByIdAndDeletedIsFalse("1");
    }
}