package com.neeraj.positionsimulator.scheduler;

import com.neeraj.positionsimulator.trip.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author neeraj on 05/10/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@Component
public class TripScheduler {

    private static Logger LOGGER = LoggerFactory.getLogger(TripScheduler.class);
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private final static String UNDERSCORE = "_";

    @Value("${trips.not.started.directory}")
    private String notStartedTripsDirectory;

    @Value("${trips.started.directory}")
    private String startedTripsDirectory;

    @Value("${vehicle.position.publish.queue}")
    private String queueName;

    @Autowired
    private JmsTemplate jmsTemplate;

    private static List<Path> filesInProcess = new ArrayList<>();

    @Scheduled(fixedDelay = 1000)
    public void scheduleNotStartedTrips() {
        Map<String, List<String>> vehicleTripCoordinates = loadTripCoordinatesForAllNotStartedVehicles();
        List<Runnable> allNotStartedTrips = new ArrayList<>();
        vehicleTripCoordinates.keySet().forEach(vehicleName ->
                allNotStartedTrips.add(
                        new Trip(vehicleTripCoordinates.get(vehicleName), vehicleName, jmsTemplate, queueName))
        );

        if (!allNotStartedTrips.isEmpty()) {
            allNotStartedTrips.forEach(this.threadPool::execute);
            LOGGER.info("Total Trips started {}", allNotStartedTrips.size());
            // Now since the trips are started so lets just move the not-started trips to the started trips directory.
            // We have all the files in {@link filesInProcess} ArrayList
            filesInProcess.forEach(filePath -> {
                try {
                    Files.move(filePath, Paths.get(startedTripsDirectory, filePath.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.error("Error while Moving {} to {} directory and error is {}", filePath.getFileName().toAbsolutePath(), startedTripsDirectory, e);
                }
                filesInProcess = new ArrayList<>();
            });
        }
    }

    private Map<String, List<String>> loadTripCoordinatesForAllNotStartedVehicles() {
        Map<String, List<String>> vehicleTripCoordinates = new HashMap<>();
        try {
            Files.walk(Paths.get(notStartedTripsDirectory))
                    .filter(Files::isRegularFile)
                    .forEach(tripFile -> {
                        filesInProcess.add(tripFile);
                        // File Name is in this format
                        // {vehicleName}_{source}_{destination}_{timestamp}
                        String[] fileNameDetails = tripFile.getFileName().toString().split(UNDERSCORE);
                        InputStream inputStream = null;
                        try {
                            inputStream = Files.newInputStream(tripFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try (Scanner scanner = new Scanner(inputStream)) {
                            List<String> thisVehicleCoordinates = new ArrayList<>();
                            while (scanner.hasNextLine()) {
                                thisVehicleCoordinates.add(scanner.nextLine());
                            }
                            vehicleTripCoordinates.put(fileNameDetails[0], thisVehicleCoordinates);
                            LOGGER.info("TRIP DETAILS fetched FOR :: {}", fileNameDetails[0]);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vehicleTripCoordinates;
    }
}
