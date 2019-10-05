package com.neeraj.positionsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = com.neeraj.positionsimulator.service.RemoteService.class)
public class VehiclePositionSimulatorApplication {

    public static void main(String[] args) {
//        try (ConfigurableApplicationContext context = SpringApplication.run(VehiclePositionSimulatorApplication.class, args)) {
//			TripSimulator tripSimulator = context.getBean(TripSimulator.class);
//
//			Thread mainThread = new Thread(tripSimulator);
//			mainThread.start();
//        }
        SpringApplication.run(VehiclePositionSimulatorApplication.class, args);
    }

}
