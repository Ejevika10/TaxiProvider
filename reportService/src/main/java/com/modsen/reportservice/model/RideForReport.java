package com.modsen.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideForReport {
    private long id;

    private String sourceAddress;

    private String destinationAddress;

    private String rideState;

    private Date rideDateTime;

    private Double rideCost;

    private Integer rating;

    private String comment;
}
