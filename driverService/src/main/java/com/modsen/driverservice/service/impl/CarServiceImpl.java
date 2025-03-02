package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.util.AppConstants;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.CarService;
import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.modsen.driverservice.util.AppConstants.CAR_CACHE_NAME;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    private final DriverRepository driverRepository;

    private final CarMapper carMapper;

    private final CarListMapper carListMapper;

    private final PageMapper pageMapper;

    @Override
    public List<CarResponseDto> getAllCars() {
        List<Car> cars = carRepository.findAllByDeletedIsFalse();
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public PageDto<CarResponseDto> getPageCars(Integer offset, Integer limit) {
        Page<CarResponseDto> pageCars = carRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit))
                .map(carMapper::toCarResponseDTO);
        return pageMapper.pageToDto(pageCars);
    }

    @Override
    public List<CarResponseDto> getAllCarsByDriverId(UUID driverId) {
        List<Car> cars = carRepository.findAllByDriverIdAndDeletedIsFalse(driverId);
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public PageDto<CarResponseDto> getPageCarsByDriverId(UUID driverId, Integer offset, Integer limit) {
        Page<CarResponseDto> pageCars = carRepository.findAllByDriverIdAndDeletedIsFalse(driverId, PageRequest.of(offset, limit))
                .map(carMapper::toCarResponseDTO);
        return pageMapper.pageToDto(pageCars);
    }

    @Override
    @Cacheable(value = CAR_CACHE_NAME, key = "#id")
    public CarResponseDto getCarById(Long id) {
        Car car = findCarByIdOrThrow(id);
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Cacheable(value = CAR_CACHE_NAME, key = "#result.id()")
    public CarResponseDto getCarByNumber(String number) {
        Car car = carRepository.findByNumberAndDeletedIsFalse(number)
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    @CachePut(value = CAR_CACHE_NAME, key = "#result.id()")
    public CarResponseDto addCar(CarRequestDto carRequestDTO) {
        Driver driver = findDriverByIdOrThrow(UUID.fromString(carRequestDTO.driverId()));
        if (carRepository.existsByNumberAndDeletedIsFalse(carRequestDTO.number())) {
            throw new DuplicateFieldException(AppConstants.CAR_NUMBER_EXIST);
        }
        Car carToSave = carMapper.toCar(carRequestDTO);
        carToSave.setDeleted(false);
        carToSave.setDriver(driver);
        Car car = carRepository.save(carToSave);
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    @CachePut(value = CAR_CACHE_NAME, key = "#id")
    public CarResponseDto updateCar(Long id, CarRequestDto carRequestDTO) {
        Car carToSave = findCarByIdOrThrow(id);
        Driver driver = findDriverByIdOrThrow(UUID.fromString(carRequestDTO.driverId()));
        Optional<Car> existingCar = carRepository.findByNumberAndDeletedIsFalse(carRequestDTO.number());
        if(existingCar.isPresent() && !existingCar.get().getId().equals(id)) {
            throw new DuplicateFieldException(AppConstants.CAR_NUMBER_EXIST);
        }
        carMapper.updateCar(carToSave, carRequestDTO);
        carToSave.setDriver(driver);
        Car car = carRepository.save(carToSave);
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    @CacheEvict(value = CAR_CACHE_NAME, key = "#id")
    public void deleteCar(Long id) {
        Car car = findCarByIdOrThrow(id);
        car.setDeleted(true);
        carRepository.save(car);
    }

    private Car findCarByIdOrThrow(Long id) {
        return carRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
    }

    private Driver findDriverByIdOrThrow(UUID id) {
        return driverRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.DRIVER_NOT_FOUND));
    }
}
