package com.modsen.passengerservice.repository;

import com.modsen.passengerservice.model.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    boolean existsByIdAndDeletedIsFalse(UUID id);

    boolean existsByEmailAndDeletedIsFalse(String email);

    Optional<Passenger> findByIdAndDeletedIsFalse(UUID id);

    Optional<Passenger> findByEmailAndDeletedIsFalse(String email);

    List<Passenger> findAllByDeletedIsFalse();

    Page<Passenger> findAllByDeletedIsFalse(Pageable pageable);
}
