package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.util.AppConstants;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    private final DriverListMapper driverListMapper;

    private final PageMapper pageMapper;

    private final MessageSource messageSource;

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
    public DriverResponseDto getDriverById(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.DRIVER_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    public DriverResponseDto getDriverByEmail(String email) {
        Driver driver = driverRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.DRIVER_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    public DriverResponseDto createDriver(DriverRequestDto driverRequestDTO) {
        if (driverRepository.existsByEmailAndDeletedIsFalse(driverRequestDTO.email())) {
            throw new DuplicateFieldException(
                    messageSource.getMessage(AppConstants.DRIVER_EMAIL_EXIST, new Object[]{}, LocaleContextHolder.getLocale()));
        }
        Driver driverToSave = driverMapper.toDriver(driverRequestDTO);
        driverToSave.setDeleted(false);
        Driver driver = driverRepository.save(driverToSave);
        driver.setDeleted(false);
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    @Transactional
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDTO) {
        if (driverRepository.existsByIdAndDeletedIsFalse(id)) {
            Optional<Driver> existingDriver = driverRepository.findByEmailAndDeletedIsFalse(driverRequestDTO.email());
            if(existingDriver.isPresent() && !existingDriver.get().getId().equals(id)) {
                throw new DuplicateFieldException(
                        messageSource.getMessage(AppConstants.DRIVER_EMAIL_EXIST, new Object[]{}, LocaleContextHolder.getLocale()));
            }
            Driver driverToSave = driverMapper.toDriver(driverRequestDTO);
            driverToSave.setId(id);
            driverToSave.setDeleted(false);
            Driver driver = driverRepository.save(driverToSave);
            return driverMapper.toDriverResponseDTO(driver);
        }
        throw new NotFoundException(
                messageSource.getMessage(AppConstants.DRIVER_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.DRIVER_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        driver.setDeleted(true);
        driverRepository.save(driver);
    }
}
