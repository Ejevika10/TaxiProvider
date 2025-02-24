package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverUpdateRequestDto;
import com.modsen.driverservice.dto.UserDeleteRequestDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.dto.UserUpdateRequestDto;
import com.modsen.driverservice.service.RabbitService;
import com.modsen.driverservice.util.AppConstants;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.modsen.driverservice.util.AppConstants.DRIVER_CACHE_NAME;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    private final DriverListMapper driverListMapper;

    private final RabbitService rabbitService;

    private final PageMapper pageMapper;

    @Override
    public List<DriverResponseDto> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAllByDeletedIsFalse();
        return driverListMapper.toDriverResponseDTOList(drivers);
    }

    @Override
    public PageDto<DriverResponseDto> getPageDrivers(Integer offset, Integer limit) {
        Page<DriverResponseDto> pageDrivers = driverRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit))
                .map(driverMapper::toDriverResponseDTO);
        return pageMapper.pageToDto(pageDrivers);
    }

    @Override
    @Cacheable(value = DRIVER_CACHE_NAME, key = "#id")
    public DriverResponseDto getDriverById(UUID id) {
        Driver driver = findByIdOrThrow(id);
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Cacheable(value = DRIVER_CACHE_NAME, key = "#result.id()")
    public DriverResponseDto getDriverByEmail(String email) {
        Driver driver = driverRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new NotFoundException(AppConstants.DRIVER_NOT_FOUND));
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    @CachePut(value = DRIVER_CACHE_NAME, key = "#result.id()")
    public DriverResponseDto createDriver(DriverCreateRequestDto driverRequestDTO) {
        if (driverRepository.existsByEmailAndDeletedIsFalse(driverRequestDTO.email())) {
            throw new DuplicateFieldException(AppConstants.DRIVER_EMAIL_EXIST);
        }
        Driver driverToSave = driverMapper.toDriver(driverRequestDTO);
        Driver driver = driverRepository.save(driverToSave);
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    @CachePut(value = DRIVER_CACHE_NAME, key = "#id")
    public DriverResponseDto updateDriver(UUID id, DriverUpdateRequestDto driverRequestDTO) {
        Driver driverToSave = findByIdOrThrow(id);
        Optional<Driver> existingDriver = driverRepository.findByEmailAndDeletedIsFalse(driverRequestDTO.email());
        if(existingDriver.isPresent() && !existingDriver.get().getId().equals(id)) {
            throw new DuplicateFieldException(AppConstants.DRIVER_EMAIL_EXIST);
        }
        driverMapper.updateDriver(driverToSave, driverRequestDTO);
        Driver driver = driverRepository.save(driverToSave);

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(driver.getId().toString(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone());
        rabbitService.sendUpdateMessage(EXCHANGE_NAME, UPDATE_ROUTING_KEY, userUpdateRequestDto);

        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    @CachePut(value = DRIVER_CACHE_NAME, key = "#result.id()")
    public DriverResponseDto updateRating(UserRatingDto userRatingDto) {
        Driver driverToSave = findByIdOrThrow(userRatingDto.id());
        driverToSave.setRating(userRatingDto.rating());
        Driver driver = driverRepository.save(driverToSave);
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    @CacheEvict(value = DRIVER_CACHE_NAME, key = "#id")
    public void deleteDriver(UUID id) {
        Driver driver = findByIdOrThrow(id);
        driver.setDeleted(true);
        driverRepository.save(driver);

        UserDeleteRequestDto userDeleteRequestDto = new UserDeleteRequestDto(id.toString());
        rabbitService.sendDeleteMessage(EXCHANGE_NAME, DELETE_ROUTING_KEY, userDeleteRequestDto);
    }

    private Driver findByIdOrThrow(UUID id) {
        return driverRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.DRIVER_NOT_FOUND));
    }
}
