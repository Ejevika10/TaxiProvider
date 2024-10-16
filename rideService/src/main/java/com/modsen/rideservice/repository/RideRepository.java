package com.modsen.rideservice.repository;

import com.modsen.rideservice.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    Page<Ride> findAll(Pageable pageable);

    List<Ride> findAllByDriverId(Long driverId);

    Page<Ride> findAllByDriverId(Long driverId, Pageable pageable);

    List<Ride> findAllByPassengerId(Long passengerId);

    Page<Ride> findAllByPassengerId(Long passengerId, Pageable pageable);

}
