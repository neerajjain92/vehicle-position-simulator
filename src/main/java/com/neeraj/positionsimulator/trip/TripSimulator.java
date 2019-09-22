package com.neeraj.positionsimulator.trip;

import com.neeraj.positionsimulator.VehiclePositionSimulatorApplication;
import com.neeraj.positionsimulator.utils.VehicleNamesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * @author neeraj on 22/09/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@Component
public class TripSimulator implements Runnable {

    private static Logger TRIP_LOGGER = LoggerFactory.getLogger(TripSimulator.class);
    private final static String TRACKS = "/tracks/";
    private ExecutorService threadPool;

    @Value("${vehicle.position.publish.queue}")
    private String queueName;

    @Autowired
    private JmsTemplate jmsTemplate;


    @Override
    public void run() {
        TRIP_LOGGER.info("Trip Simulator started......");
        try {
            this.runTripSimulation();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * For Each Vehicle, a thread is started which simulates a trip from source to destination of that vehicle.
     * When all Vehicles are completed we will start over again.
     * This trip is being simulated by the dummy co-ordinates available in the /resources/coordinates directory.
     *
     * @throws InterruptedException
     */
    private void runTripSimulation() throws InterruptedException {
        Map<String, List<String>> tripDetails = loadTripDataForAllVehicles();
        threadPool = Executors.newCachedThreadPool();
        boolean stillRunning = true;
        while (stillRunning) {
            List<Callable<Object>> allTrips = new ArrayList<>();
            tripDetails.keySet().forEach(tripName -> {
                allTrips.add(new Trip(tripDetails.get(tripName), tripName, this.jmsTemplate, this.queueName));
            });
            this.threadPool.invokeAll(allTrips);
            if (threadPool.isShutdown()) {
                stillRunning = false;
            }
        }
    }

    /**
     * Read trip related data from Resources directory
     *
     * @return
     */
    private Map<String, List<String>> loadTripDataForAllVehicles() {
        final Map<String, List<String>> tripDetails = new HashMap<>();
        PathMatchingResourcePatternResolver path = new PathMatchingResourcePatternResolver();

        try {

            Stream.of(path.getResources(TRACKS + "*"))
                    .forEach(resource -> {
                        try {
                            URL resourceURL = resource.getURL();
                            File file = new File(resourceURL.getFile());

                            // Get the Vehicle Name from the file, and keep it as the key in tripDetails Map.
                            String vehicleName = VehicleNamesUtil.prettifyName(file.getName());
                            InputStream inputStream = VehiclePositionSimulatorApplication.class
                                    .getResourceAsStream(TRACKS + file.getName());

                            try (Scanner scanner = new Scanner(inputStream)) {
                                List<String> thisVehicleCoordinates = new ArrayList<>();
                                while (scanner.hasNextLine()) {
                                    thisVehicleCoordinates.add(scanner.nextLine());
                                }
                                tripDetails.put(vehicleName, thisVehicleCoordinates);
                                TRIP_LOGGER.info("TRIP DETAILS fetched FOR :: {}", vehicleName);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Collections.unmodifiableMap(tripDetails);
    }
}
