package com.neeraj.positionsimulator;

import com.neeraj.positionsimulator.trip.TripSimulator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class VehiclePositionSimulatorApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(VehiclePositionSimulatorApplication.class, args)) {
			TripSimulator tripSimulator = context.getBean(TripSimulator.class);

			Thread mainThread = new Thread(tripSimulator);
			mainThread.start();
        }
    }

}
