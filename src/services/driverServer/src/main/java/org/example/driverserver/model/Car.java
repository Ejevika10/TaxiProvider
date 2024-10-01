package org.example.driverserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @ManyToOne
    @JoinColumn(name = "driver_id")
    Driver driver;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(unique = true, nullable = false)
    private String number;
    @Column(nullable = false)
    private boolean deleted = Boolean.FALSE;
}
