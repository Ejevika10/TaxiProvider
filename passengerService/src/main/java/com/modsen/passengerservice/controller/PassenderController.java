package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.PageDTO;
import com.modsen.passengerservice.dto.PassengerRequestDTO;
import com.modsen.passengerservice.dto.PassengerResponseDTO;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassenderController {
    private final PassengerService passengerService;

    @GetMapping("/all")
    public ResponseEntity<List<PassengerResponseDTO>> getPassengers() {
        List<PassengerResponseDTO> passengers = passengerService.getAllPassengers();
        return new ResponseEntity<>(passengers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageDTO<PassengerResponseDTO>> getPagePassengers(@RequestParam(defaultValue = "0") Integer offset, @RequestParam (defaultValue = "5") Integer limit) {
        PageDTO<PassengerResponseDTO> page = passengerService.getPagePassengers(offset, limit);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassenger(@PathVariable Long id) {
        PassengerResponseDTO passenger = passengerService.getPassengerById(id);
        return new ResponseEntity<>(passenger, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(@RequestBody PassengerRequestDTO passengerRequestDTO) {
        PassengerResponseDTO passenger = passengerService.addPassenger(passengerRequestDTO);
        return new ResponseEntity<>(passenger, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<PassengerResponseDTO> updatePassenger(@RequestBody PassengerRequestDTO passengerRequestDTO) {
        PassengerResponseDTO passenger = passengerService.updatePassenger(passengerRequestDTO);
        return new ResponseEntity<>(passenger, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
