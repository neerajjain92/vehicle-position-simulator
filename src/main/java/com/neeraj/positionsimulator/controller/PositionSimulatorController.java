package com.neeraj.positionsimulator.controller;

import com.neeraj.positionsimulator.dto.TripDetails;
import com.neeraj.positionsimulator.service.PositionSimulatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author neeraj on 05/10/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@RestController
@RequestMapping("/simulator")
public class PositionSimulatorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionSimulatorController.class);

    @Autowired
    private PositionSimulatorService simulatorService;


    @PostMapping
    public ResponseEntity addVehiclesToMap(@Valid @RequestBody TripDetails tripDetails) {
        LOGGER.info("Adding Trip {} to the Position Simulator", tripDetails);
        simulatorService.addTripDetails(tripDetails);
        return ResponseEntity.ok().build();
    }

}
