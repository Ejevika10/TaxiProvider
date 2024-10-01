package org.example.driverserver.service.Implementation;

import org.example.driverserver.dto.DriverRequestDTO;
import org.example.driverserver.dto.DriverResponseDTO;
import org.example.driverserver.exception.DuplicateFieldException;
import org.example.driverserver.exception.NotFoundException;
import org.example.driverserver.mapper.DriverListMapper;
import org.example.driverserver.mapper.DriverMapper;
import org.example.driverserver.model.Driver;
import org.example.driverserver.repository.DriverRepository;
import org.example.driverserver.service.DriverService;
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
