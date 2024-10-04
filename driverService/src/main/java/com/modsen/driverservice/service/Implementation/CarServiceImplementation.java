package com.modsen.driverservice.service.Implementation;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImplementation implements CarService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private CarListMapper carListMapper;

    @Override
    public List<CarResponseDTO> getAllCars() {
        List<Car> cars = carRepository.findAllByDeletedIsFalse();
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public List<CarResponseDTO> getAllCarsByDriverId(Long driverId) {
        List<Car> cars = carRepository.findAllByDriverIdAndDeletedIsFalse(driverId);
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public CarResponseDTO getCarById(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Car not found", 404L));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDTO addCar(CarRequestDTO carRequestDTO) {
        if (carRepository.existsByNumberAndDeletedIsFalse(carRequestDTO.getNumber())) {
            throw new DuplicateFieldException("Car with this number already exist", 400L);
        }
        Car car = carRepository.save(carMapper.toCar(carRequestDTO));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDTO updateCar(CarRequestDTO carRequestDTO) {
        if (carRepository.existsByIdAndDeletedIsFalse(carRequestDTO.getId())) {
            Car car = carRepository.save(carMapper.toCar(carRequestDTO));
            return carMapper.toCarResponseDTO(car);
        }
        throw new NotFoundException("Car not found", 404L);
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Car not found", 404L));
        car.setDeleted(true);
        carRepository.save(car);
    }
}
