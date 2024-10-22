package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.util.AppConstants;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    private final DriverRepository driverRepository;

    private final CarMapper carMapper;

    private final CarListMapper carListMapper;

    private final PageMapper pageMapper;

    private final MessageSource messageSource;

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
    public List<CarResponseDto> getAllCarsByDriverId(Long driverId) {
        List<Car> cars = carRepository.findAllByDriverIdAndDeletedIsFalse(driverId);
        return carListMapper.toCarResponseDTOList(cars);
    }

    @Override
    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDto getCarByNumber(String number) {
        Car car = carRepository.findByNumberAndDeletedIsFalse(number)
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    public CarResponseDto addCar(CarRequestDto carRequestDTO) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(carRequestDTO.driverId())
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
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
    public CarResponseDto updateCar(Long id, CarRequestDto carRequestDTO) {
        Driver driver = driverRepository.findByIdAndDeletedIsFalse(carRequestDTO.driverId())
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.DRIVER_NOT_FOUND, new Object[]{}, LocaleContextHolder.getLocale())));
        if (carRepository.existsByIdAndDeletedIsFalse(id)) {
            Optional<Car> existingCar = carRepository.findByNumberAndDeletedIsFalse(carRequestDTO.number());
            if(existingCar.isPresent() && !existingCar.get().getId().equals(id)) {
                throw new DuplicateFieldException(AppConstants.CAR_NUMBER_EXIST);
            }
            Car carToSave = carMapper.toCar(carRequestDTO);
            carToSave.setId(id);
            carToSave.setDeleted(false);
            carToSave.setDriver(driver);
            Car car = carRepository.save(carToSave);
            return carMapper.toCarResponseDTO(car);
        }

        throw new NotFoundException(AppConstants.CAR_NOT_FOUND);
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.CAR_NOT_FOUND));
        car.setDeleted(true);
        carRepository.save(car);
    }
}
