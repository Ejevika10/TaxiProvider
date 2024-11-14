package com.modsen.driverservice.repository;

import com.modsen.driverservice.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByIdAndDeletedIsFalse(Long id);

    boolean existsByNumberAndDeletedIsFalse(String number);

    Optional<Car> findByIdAndDeletedIsFalse(Long id);

    Optional<Car> findByNumberAndDeletedIsFalse(String number);

    List<Car> findAllByDeletedIsFalse();

    Page<Car> findAllByDeletedIsFalse(Pageable pageable);

    List<Car> findAllByDriverIdAndDeletedIsFalse(Long id);

    Page<Car> findAllByDriverIdAndDeletedIsFalse(Long id, Pageable pageable);
}
