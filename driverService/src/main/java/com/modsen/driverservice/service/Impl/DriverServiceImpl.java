package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.dto.PageDTO;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    private final DriverListMapper driverListMapper;

    private final PageMapper pageMapper;

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAllByDeletedIsFalse();
        return driverListMapper.toDriverResponseDTOList(drivers);
    }

    @Override
    public PageDTO<DriverResponseDTO> getPageDrivers(Integer offset, Integer limit) {
        Page<DriverResponseDTO> pageDrivers = driverRepository.findAllByDeletedIsFalse(PageRequest.of(offset,limit)).map(driverMapper::toDriverResponseDTO);
        return pageMapper.pageToDto(pageDrivers);
    }

    @Override
    public DriverResponseDTO getDriverById(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Driver not found", 404L));
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    public DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO) {
        if(driverRepository.existsByEmailAndDeletedIsFalse(driverRequestDTO.getEmail())) {
            throw new DuplicateFieldException("Driver with this email already exists", 400L);
        }
        Driver driver = driverRepository.save(driverMapper.toDriver(driverRequestDTO));
        return driverMapper.toDriverResponseDTO(driver);
    }

    @Override
    public DriverResponseDTO updateDriver(DriverRequestDTO driverRequestDTO) {
        if(driverRepository.existsByIdAndDeletedIsFalse(driverRequestDTO.getId())) {
            Driver driver = driverRepository.save(driverMapper.toDriver(driverRequestDTO));
            return driverMapper.toDriverResponseDTO(driver);
        }
        return null;
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Driver not found", 404L));
        driver.setDeleted(true);
        driverRepository.save(driver);
    }
}
