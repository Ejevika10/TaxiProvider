package com.modsen.rideservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private Long driverId;

    @Column(nullable = false)
    private Long passengerId;

    @Column(nullable = false)
    private String sourceAddress;

    @Column(nullable = false)
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideState rideState;

    @Column(nullable = false)
    private LocalDateTime rideDateTime;

    @Column(nullable = false)
    private BigInteger rideCost;
}
