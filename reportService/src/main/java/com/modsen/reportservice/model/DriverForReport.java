package com.modsen.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverForReport {
    private UUID id;

    private String name;

    private String email;

    private String phone;

    private Double rating;
}
