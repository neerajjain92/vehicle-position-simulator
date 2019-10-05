package com.neeraj.positionsimulator.service;

import com.neeraj.positionsimulator.dto.TripDetails;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author neeraj on 05/10/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@Service
public class PositionSimulatorService {

    @Autowired
    private RemoteService remoteService;

    @Value("${trips.not.started.directory}")
    private String notStartedTripsDirectory;

    private static Logger LOGGER = LoggerFactory.getLogger(PositionSimulatorService.class);


    public String addTripDetails(TripDetails tripDetails) {
        ResponseEntity<Resource> resourceResponseEntity = remoteService
                .getDirectionsInFile(tripDetails.getSource(), tripDetails.getDestination(), tripDetails.getVehicleName());

        LOGGER.info("File Downloaded from TripDirections Service is {}", resourceResponseEntity);
        ClassLoader classLoader = getClass().getClassLoader();
        Resource resource = resourceResponseEntity.getBody();

        try {
            InputStream inputStream = resource.getInputStream();
            File directionsFile = new File(notStartedTripsDirectory + resource.getFilename());
            try (OutputStream outputStream = new FileOutputStream(directionsFile)) {
                IOUtils.copyLarge(inputStream, outputStream);
            }
        } catch (IOException e) {
            LOGGER.error("Error while creating directions file {}", e);
        }
        return resource.getFilename();
    }
}
