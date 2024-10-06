package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.dto.PageDTO;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    private final CarMapper carMapper;

    private final CarListMapper carListMapper;

    private final PageMapper pageMapper;

    @Override
    public List<CarResponseDTO> getAllCars() {
        List<Car> cars = carRepository.findAllByDeletedIsFalse();
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public PageDTO<CarResponseDTO> getPageCars(Integer offset, Integer limit) {
        Page<CarResponseDTO> pageCars = carRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit)).map(carMapper::toCarResponseDTO);
        return pageMapper.pageToDto(pageCars);
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
    @Transactional
    public CarResponseDTO addCar(CarRequestDTO carRequestDTO) {
        if (carRepository.existsByNumberAndDeletedIsFalse(carRequestDTO.getNumber())) {
            throw new DuplicateFieldException("Car with this number already exist", 400L);
        }
        Car car = carRepository.save(carMapper.toCar(carRequestDTO));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    public CarResponseDTO updateCar(CarRequestDTO carRequestDTO) {
        if (carRepository.existsByIdAndDeletedIsFalse(carRequestDTO.getId())) {
            Car car = carRepository.save(carMapper.toCar(carRequestDTO));
            return carMapper.toCarResponseDTO(car);
        }
        throw new NotFoundException("Car not found", 404L);
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Car not found", 404L));
        car.setDeleted(true);
        carRepository.save(car);
    }
}
