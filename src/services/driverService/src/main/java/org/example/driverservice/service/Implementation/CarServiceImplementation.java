package org.example.driverservice.service.Implementation;

import org.example.driverservice.dto.CarRequestDTO;
import org.example.driverservice.dto.CarResponseDTO;
import org.example.driverservice.exception.DuplicateFieldException;
import org.example.driverservice.exception.NotFoundException;
import org.example.driverservice.mapper.CarListMapper;
import org.example.driverservice.mapper.CarMapper;
import org.example.driverservice.model.Car;
import org.example.driverservice.repository.CarRepository;
import org.example.driverservice.service.CarService;
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
