package com.modsen.driverservice.service.Impl;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.exception.DuplicateFieldException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.CarListMapper;
import com.modsen.driverservice.mapper.CarMapper;
import com.modsen.driverservice.mapper.PageMapper;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.repository.CarRepository;
import com.modsen.driverservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

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
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("car.notfound", new Object[]{}, Locale.US)));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public CarResponseDto getCarByNumber(String number) {
        Car car = carRepository.findByNumberAndDeletedIsFalse(number)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("car.notfound", new Object[]{}, Locale.US)));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    public CarResponseDto addCar(CarRequestDto carRequestDTO) {
        if (carRepository.existsByNumberAndDeletedIsFalse(carRequestDTO.number())) {
            throw new DuplicateFieldException(
                    messageSource.getMessage("car.number.exist", new Object[]{}, Locale.US));
        }
        Car car = carRepository.save(carMapper.toCar(carRequestDTO));
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    @Transactional
    public CarResponseDto updateCar(Long id, CarRequestDto carRequestDTO) {
        if (carRepository.existsByIdAndDeletedIsFalse(id)) {
            Optional<Car> existingCar = carRepository.findByNumberAndDeletedIsFalse(carRequestDTO.number());
            if(existingCar.isPresent() && !existingCar.get().getId().equals(id)) {
                throw new DuplicateFieldException(
                        messageSource.getMessage("car.number.exist", new Object[]{}, Locale.US));
            }
            Car carToSave = carMapper.toCar(carRequestDTO);
            carToSave.setId(id);
            Car car = carRepository.save(carToSave);
            return carMapper.toCarResponseDTO(car);
        }

        throw new NotFoundException(
                messageSource.getMessage("car.notfound", new Object[]{}, Locale.US));
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("car.notfound", new Object[]{}, Locale.US)));
        car.setDeleted(true);
        carRepository.save(car);
    }
}
