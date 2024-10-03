package org.example.driverserver.controller;

import org.example.driverserver.dto.DriverRequestDTO;
import org.example.driverserver.dto.DriverResponseDTO;
import org.example.driverserver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        DriverResponseDTO driver = driverService.getDriverById(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driver = driverService.createDriver(driverRequestDTO);
        return new ResponseEntity<>(driver, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DriverResponseDTO> updateDriver(@RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driver = driverService.updateDriver(driverRequestDTO);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
