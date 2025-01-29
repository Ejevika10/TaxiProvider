package com.modsen.rideservice.repository;

import com.modsen.rideservice.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, Long> {

    Page<Ride> findAll(Pageable pageable);

    List<Ride> findAllByDriverId(UUID driverId);

    Page<Ride> findAllByDriverId(UUID driverId, Pageable pageable);

    List<Ride> findAllByPassengerId(UUID passengerId);

    Page<Ride> findAllByPassengerId(UUID passengerId, Pageable pageable);

}
