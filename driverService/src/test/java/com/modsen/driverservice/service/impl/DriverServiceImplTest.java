package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.DriverUpdateRequestDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.RabbitService;
import com.modsen.driverservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
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

import static com.modsen.driverservice.util.TestData.DRIVER_ID;
import static com.modsen.driverservice.util.TestData.DRIVER_ID_2;
import static com.modsen.driverservice.util.TestData.LIMIT_VALUE;
import static com.modsen.driverservice.util.TestData.OFFSET_VALUE;
import static com.modsen.driverservice.util.TestData.getDriver;
import static com.modsen.driverservice.util.TestData.getDriverBuilder;
import static com.modsen.driverservice.util.TestData.getDriverCreateRequestDto;
import static com.modsen.driverservice.util.TestData.getDriverList;
import static com.modsen.driverservice.util.TestData.getDriverResponseDto;
import static com.modsen.driverservice.util.TestData.getDriverResponseDtoBuilder;
import static com.modsen.driverservice.util.TestData.getDriverResponseDtoList;
import static com.modsen.driverservice.util.TestData.getDriverUpdateRequestDto;
import static com.modsen.driverservice.util.TestData.getUserRatingDto;
import static com.modsen.driverservice.util.TestData.NEW_RATING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private RabbitService rabbitService;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void getAllDrivers() {
        //Arrange
        List<Driver> driverList = getDriverList();
        List<DriverResponseDto> driverResponseDtoList = getDriverResponseDtoList();
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
        List<Driver> driverList = getDriverList();
        List<DriverResponseDto> driverResponseDtoList = getDriverResponseDtoList();
        PageRequest pageRequest = PageRequest.of(OFFSET_VALUE, LIMIT_VALUE);
        PageDto<DriverResponseDto> expected = new PageDto<>(OFFSET_VALUE, LIMIT_VALUE, 1, driverResponseDtoList.size(), driverResponseDtoList);
        Page<Driver> driverPage = new PageImpl<>(driverList, pageRequest, 1);
        when(driverRepository.findAllByDeletedIsFalse(pageRequest)).thenReturn(driverPage);
        when(pageMapper.pageToDto(any(Page.class))).thenReturn(expected);

        //Act
        PageDto<DriverResponseDto> actual = driverService.getPageDrivers(OFFSET_VALUE, LIMIT_VALUE);

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
        Driver driver = getDriver();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(driverMapper.toDriverResponseDTO(driver)).thenReturn(driverResponseDto);

        //Act
        DriverResponseDto actual = driverService.getDriverById(driver.getId());

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driver.getId());
        verify(driverMapper).toDriverResponseDTO(driver);
        assertEquals(actual, driverResponseDto);
    }

    @Test
    void getDriverById_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.getDriverById(DRIVER_ID),
                MessageConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(DRIVER_ID);
    }

    @Test
    void getDriverByEmail_ExistingEmail_ReturnsValidDto() {
        //Arrange
        Driver driver = getDriver();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driver));
        when(driverMapper.toDriverResponseDTO(driver)).thenReturn(driverResponseDto);

        //Act
        DriverResponseDto actual = driverService.getDriverByEmail(driver.getEmail());

        //Assert
        assertEquals(actual, driverResponseDto);
        verify(driverRepository).findByEmailAndDeletedIsFalse(driver.getEmail());
        verify(driverMapper).toDriverResponseDTO(driver);
    }

    @Test
    void getDriverByEmail_NonExistingEmail_ReturnsNotFoundException() {
        //Arrange
        Driver driver = getDriver();
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.getDriverByEmail(driver.getEmail()),
                MessageConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByEmailAndDeletedIsFalse(driver.getEmail());
    }

    @Test
    void createDriver_UniqueEmail_ReturnsValidDto() {
        //Arrange
        DriverCreateRequestDto driverRequestDto = getDriverCreateRequestDto();
        Driver driver = getDriver();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        when(driverRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);
        when(driverMapper.toDriver(driverRequestDto)).thenReturn(driver);
        when(driverMapper.toDriverResponseDTO(driver)).thenReturn(driverResponseDto);
        when(driverRepository.save(driver)).thenReturn(driver);

        //Act
        DriverResponseDto actual = driverService.createDriver(driverRequestDto);

        //Assert
        verify(driverRepository).existsByEmailAndDeletedIsFalse(driverRequestDto.email());
        verify(driverMapper).toDriver(driverRequestDto);
        verify(driverRepository).save(driver);
        verify(driverMapper).toDriverResponseDTO(driver);
        assertEquals(actual, driverResponseDto);
    }

    @Test
    void createDriver_NonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        DriverCreateRequestDto driverRequestDto = getDriverCreateRequestDto();
        when(driverRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> driverService.createDriver(driverRequestDto),
                MessageConstants.DRIVER_EMAIL_EXIST);
        verify(driverRepository).existsByEmailAndDeletedIsFalse(driverRequestDto.email());
    }

    @Test
    void updateDriver_ExistingIdUniqueEmail_ReturnsValidDto() {
        //Arrange
        DriverUpdateRequestDto driverRequestDto = getDriverUpdateRequestDto();
        DriverResponseDto driverResponseDto = getDriverResponseDto();
        Driver driver = getDriver();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDriverResponseDTO(driver)).thenReturn(driverResponseDto);

        //Act
        DriverResponseDto actual = driverService.updateDriver(driver.getId(), driverRequestDto);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driver.getId());
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverRequestDto.email());
        verify(driverRepository).save(driver);
        verify(driverMapper).toDriverResponseDTO(driver);
        assertEquals(actual, driverResponseDto);
    }

    @Test
    void updateDriver_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        DriverUpdateRequestDto driverRequestDto = getDriverUpdateRequestDto();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.updateDriver(DRIVER_ID, driverRequestDto),
                MessageConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(DRIVER_ID);
    }

    @Test
    void updateDriver_ExistingIdNonUniqueEmail_ReturnsDuplicateFieldException() {
        //Arrange
        Driver driver = getDriver();
        Driver driverWithSameEmail = getDriverBuilder()
                .id(DRIVER_ID_2)
                .build();
        DriverUpdateRequestDto driverRequestDto = getDriverUpdateRequestDto();

        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(driverRepository.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(driverWithSameEmail));

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> driverService.updateDriver(driver.getId(), driverRequestDto),
                MessageConstants.DRIVER_EMAIL_EXIST);
        verify(driverRepository).findByIdAndDeletedIsFalse(driver.getId());
        verify(driverRepository).findByEmailAndDeletedIsFalse(driverRequestDto.email());
    }

    @Test
    void deleteDriver_ExistingId() {
        //Arrange
        Driver driver = getDriver();
        when(driverRepository.findByIdAndDeletedIsFalse(driver.getId())).thenReturn(Optional.of(driver));
        when(driverRepository.save(driver)).thenReturn(driver);

        //Act
        driverService.deleteDriver(driver.getId());

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driver.getId());
        verify(driverRepository).save(driver);
        assertTrue(driver.getDeleted());
    }

    @Test
    void deleteDriver_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.deleteDriver(DRIVER_ID),
                MessageConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(DRIVER_ID);
    }

    @Test
    void updateRating_ExistingId_ReturnsValidDto() {
        //Arrange
        Driver driver = getDriver();
        UserRatingDto driverRating = getUserRatingDto();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.of(driver));
        when(driverRepository.save(driver)).thenReturn(driver);
        DriverResponseDto driverUpdatedResponseDto = getDriverResponseDtoBuilder()
                .rating(NEW_RATING)
                .build();
        when(driverMapper.toDriverResponseDTO(driver)).thenReturn(driverUpdatedResponseDto);

        //Act
        DriverResponseDto actual = driverService.updateRating(driverRating);

        //Assert
        verify(driverRepository).findByIdAndDeletedIsFalse(driverRating.id());
        verify(driverRepository).save(driver);
        verify(driverMapper).toDriverResponseDTO(driver);
        assertEquals(driverRating.rating(), actual.rating());
    }

    @Test
    void updateRating_NonExistingId_ReturnsNotFoundException() {
        //Arrange
        UserRatingDto driverRating = getUserRatingDto();
        when(driverRepository.findByIdAndDeletedIsFalse(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundException.class,
                () -> driverService.updateRating(driverRating),
                MessageConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndDeletedIsFalse(driverRating.id());
    }
}