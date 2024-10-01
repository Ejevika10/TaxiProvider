package org.example.driverserver.service.Implementation;

import org.example.driverserver.dto.CarRequestDTO;
import org.example.driverserver.dto.CarResponseDTO;
import org.example.driverserver.exception.DuplicateFieldException;
import org.example.driverserver.exception.NotFoundException;
import org.example.driverserver.mapper.CarListMapper;
import org.example.driverserver.mapper.CarMapper;
import org.example.driverserver.model.Car;
import org.example.driverserver.repository.CarRepository;
import org.example.driverserver.service.CarService;
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
    public CarResponseDTO getCarById(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Car not found", 404L));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDTO addCar(CarRequestDTO carRequestDTO) {
        if(carRepository.existsByNumberAndDeletedIsFalse(carRequestDTO.getNumber())) {
            throw new DuplicateFieldException("Car with this number already exist", 400L);
        }
        Car car = carRepository.save(carMapper.toCar(carRequestDTO));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDTO updateCar(CarRequestDTO carRequestDTO) {
        if(carRepository.existsByIdAndDeletedIsFalse(carRequestDTO.getId())) {
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
