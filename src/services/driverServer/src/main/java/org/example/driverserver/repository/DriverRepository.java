package org.example.driverserver.repository;

import org.example.driverserver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByIdAndDeletedIsFalse(Long id);
    boolean existsByEmailAndDeletedIsFalse(String email);
    Optional<Driver> findByIdAndDeletedIsFalse(Long id);
    List<Driver> findAllByDeletedIsFalse();
}
