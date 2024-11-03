package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.util.AppConstants;
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
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private DriverListMapper driverListMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private final UserRatingDto driverRating = new UserRatingDto(1L, 5.0);

    private final Driver driverA = new Driver(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0, null, false);
    private final DriverRequestDto driverARequestDto = new DriverRequestDto( "DriverA", "DriverA@email.com", 0.0, "71234567890");
    private final DriverResponseDto driverAResponseDto = new DriverResponseDto(1L, "DriverA", "DriverA@email.com", "71234567890", 0.0);

    private final Driver driverB = new Driver(2L, "DriverB", "DriverB@email.com", "71234567890", 0.0, null, false);
    private final DriverRequestDto driverBRequestDto = new DriverRequestDto( "DriverB", "DriverB@email.com", 0.0, "71234567890");
    private final DriverResponseDto driverBResponseDto = new DriverResponseDto(1L, "DriverB", "DriverB@email.com", "71234567890", 0.0);

    private final List<Driver> driverList = List.of(driverA, driverB);
    private final List<DriverResponseDto> driverResponseDtoList = List.of(driverAResponseDto, driverBResponseDto);

    @Test
    void getAllDrivers() {
        //Arrange
        when(driverRepository.findAllByDeletedIsFalse()).thenReturn(driverList);
        when(driverListMapper.toDriverResponseDTOList(driverList)).thenReturn(driverResponseDtoList);

        //Act
        List<DriverResponseDto> actual = driverService.getAllDrivers();

        //Assert
        verify(driverRepository).findAllByDeletedIsFalse();
        verify(driverListMapper).toDriverResponseDTOList(driverList);
        assertEquals(actual.size(), driverResponseDtoList.size());
        assertIterableEquals(actual, driverResponseDtoList);
    }

    @Test
    void getPageDrivers() {
        //Arrange
        int offset = 0;
        int limit = 5;
        PageRequest pageRequest = PageRequest.of(offset, limit);
        PageDto<DriverResponseDto> expected = new PageDto<>(offset, limit, 1, driverResponseDtoList.size(), driverResponseDtoList);
        Page<Driver> driverPage = new PageImpl<>(driverList, pageRequest, 1);
        when(driverRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(driverPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<DriverResponseDto> actual = driverService.getPageDrivers(offset, limit);

        //Assert
        verify(driverRepository).findAllByDeletedIsFalse(pageRequest);
        verify(driverMapper, times(actual.content().size())).toDriverResponseDTO(any(Driver.class));
        verify(pageMapper).pageToDto(any(Page.class));

        assertIterableEquals(actual.content(), expected.content());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(actual.pageSize(), expected.pageSize());
        assertEquals(actual.totalElements(), expected.totalElements());
        assertEquals(actual.totalPages(), expected.totalPages());
    }

    @Test
    void getDriverById_ExistingId_ReturnsValidDto() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(driverMapper.toDriverResponseDTO(driverA)).thenReturn(driverAResponseDto);

        //Act
        DriverResponseDto actual = driverService.getDriverById(driverA.getId());

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driverA.getId());
        verify(driverMapper).toDriverResponseDTO(driverA);
        assertEquals(actual, driverAResponseDto);
    }

    @Test
    void getDriverById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.getDriverById(3L),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void getDriverByEmail_ExistingEmail_ReturnsValidDto() {
        //Arrange
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverA));
        when(driverMapper.toDriverResponseDTO(driverA)).thenReturn(driverAResponseDto);

        //Act
        DriverResponseDto actual = driverService.getDriverByEmail(driverA.getEmail());

        //Assert
        assertEquals(actual, driverAResponseDto);
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverA.getEmail());
        verify(driverMapper).toDriverResponseDTO(driverA);
    }

    @Test
    void getDriverByEmail_NonExistingEmail_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.getDriverByEmail(driverA.getEmail()),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverA.getEmail());
    }

    @Test
    void createDriver_UniqueEmail_ReturnsValidDto() {
        //Arrange
        when(driverRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);
        when(driverMapper.toDriver(driverARequestDto)).thenReturn(driverA);
        when(driverMapper.toDriverResponseDTO(driverA)).thenReturn(driverAResponseDto);
        when(driverRepository.save(driverA)).thenReturn(driverA);

        //Act
        DriverResponseDto actual = driverService.createDriver(driverARequestDto);

        //Assert
        verify(driverRepository).existsByEmailAndDeletedIsFalse(driverARequestDto.email());
        verify(driverMapper).toDriver(driverARequestDto);
        verify(driverRepository).save(driverA);
        verify(driverMapper).toDriverResponseDTO(driverA);
        assertEquals(actual, driverAResponseDto);
    }

    @Test
    void createDriver_NonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        when(driverRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> driverService.createDriver(driverARequestDto),
                AppConstants.DRIVER_EMAIL_EXIST);
        verify(driverRepository).existsByEmailAndDeletedIsFalse(driverARequestDto.email());
    }

    @Test
    void updateDriver_ExistingIdUniqueEmail_ReturnsValidDto() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(driverRepository.save(driverA)).thenReturn(driverA);
        when(driverMapper.toDriverResponseDTO(driverA)).thenReturn(driverAResponseDto);

        //Act
        DriverResponseDto actual = driverService.updateDriver(driverA.getId(), driverARequestDto);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driverA.getId());
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverARequestDto.email());
        verify(driverRepository).save(driverA);
        verify(driverMapper).toDriverResponseDTO(driverA);
        assertEquals(actual, driverAResponseDto);
    }

    @Test
    void updateDriver_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.updateDriver(3L, driverARequestDto),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void updateDriver_ExistingIdNonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverB));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> driverService.updateDriver(driverA.getId(), driverBRequestDto),
                AppConstants.DRIVER_EMAIL_EXIST);
        verify(driverRepository).findByIdAndDeletedIsFalse(driverA.getId());
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverBRequestDto.email());
    }

    @Test
    void deleteDriver_ExistingId() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(driverA.getId())).thenReturn(Optional.of(driverA));
        when(driverRepository.save(driverA)).thenReturn(driverA);

        //Act
        driverService.deleteDriver(driverA.getId());

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driverA.getId());
        verify(driverRepository).save(driverA);
        assertTrue(driverA.getDeleted());
    }

    @Test
    void deleteDriver_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.deleteDriver(3L),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(3L);
    }

    @Test
    void updateRating_ExistingId_ReturnsValidDto() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(driverA));
        when(driverRepository.save(driverA)).thenReturn(driverA);
        DriverResponseDto driverAUpdatedResponseDto = new DriverResponseDto(1L, "DriverA", "DriverA@email.com", "71234567890", 5.0);
        when(driverMapper.toDriverResponseDTO(driverA)).thenReturn(driverAUpdatedResponseDto);

        //Act
        DriverResponseDto actual = driverService.updateRating(driverRating);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driverRating.id());
        verify(driverRepository).save(driverA);
        verify(driverMapper).toDriverResponseDTO(driverA);
        assertEquals(driverRating.rating(), actual.rating());
    }

    @Test
    void updateRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.updateRating(driverRating),
                AppConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(driverRating.id());
    }
}