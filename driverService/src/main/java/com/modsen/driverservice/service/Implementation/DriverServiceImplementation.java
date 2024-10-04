package com.modsen.driverservice.service.Implementation;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.DriverListMapper;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImplementation implements DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private DriverListMapper driverListMapper;

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAllByDeletedIsFalse();
        return driverListMapper.toDriverResponseDTOList(drivers);
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
