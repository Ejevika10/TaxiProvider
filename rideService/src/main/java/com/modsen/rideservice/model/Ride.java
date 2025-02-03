package com.modsen.rideservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private UUID driverId;

    @Column(nullable = false)
    private UUID passengerId;

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
    private Integer rideCost;
}
