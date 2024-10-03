package org.example.driverserver.repository;

import org.example.driverserver.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByIdAndDeletedIsFalse(Long id);
    boolean existsByNumberAndDeletedIsFalse(String number);
    Optional<Car> findByIdAndDeletedIsFalse(Long id);
    List<Car> findAllByDeletedIsFalse();
    List<Car> findAllByDriverIdAndDeletedIsFalse(Long id);
}
