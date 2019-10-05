package com.neeraj.positionsimulator.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author neeraj on 05/10/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@Data
public class TripDetails {

    @NotBlank
    private String source;
    @NotBlank
    private String destination;
    @NotBlank
    private String vehicleName;
}
