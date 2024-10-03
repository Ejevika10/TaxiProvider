package org.example.driverserver.controller;

import org.example.driverserver.dto.CarRequestDTO;
import org.example.driverserver.dto.CarResponseDTO;
import org.example.driverserver.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    @Autowired
    CarService carService;

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getCars() {
        List<CarResponseDTO> cars = carService.getAllCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getCar(@PathVariable Long id) {
        CarResponseDTO car = carService.getCarById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByDriverId(@PathVariable Long driverId) {
        List<CarResponseDTO> cars = carService.getAllCarsByDriverId(driverId);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO car = carService.addCar(carRequestDTO);
        return new ResponseEntity<>(car, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CarResponseDTO> updateCar(@RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO car = carService.updateCar(carRequestDTO);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CarResponseDTO> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
