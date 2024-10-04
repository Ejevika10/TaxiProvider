package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.dto.PageDTO;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageDTO<DriverResponseDTO>> getCars(@RequestParam Integer limit, @RequestParam Integer offset) {
        PageDTO<DriverResponseDTO> drivers = driverService.getPageDrivers(limit, offset);
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
