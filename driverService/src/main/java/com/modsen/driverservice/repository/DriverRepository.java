package com.modsen.driverservice.repository;

import com.modsen.driverservice.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    boolean existsByIdAndDeletedIsFalse(UUID id);

    boolean existsByEmailAndDeletedIsFalse(String email);

    Optional<Driver> findByIdAndDeletedIsFalse(UUID id);

    Optional<Driver> findByEmailAndDeletedIsFalse(String email);

    List<Driver> findAllByDeletedIsFalse();

    Page<Driver> findAllByDeletedIsFalse(Pageable pageable);
}
