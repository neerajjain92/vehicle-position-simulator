package com.neeraj.positionsimulator.trip;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A Callable Trip that sends message to the queue periodically - representing the trip of a Vehicle
 * We are implementing {@link Callable} so that we can invoke in on a executor and join on it
 *
 * @author neeraj on 22/09/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@Data
@NoArgsConstructor
public class Trip implements Callable<Object> {

    private static Logger TRIP_LOGGER = LoggerFactory.getLogger(Trip.class);
    private List<String> positions;
    private String vehicleName;
    private JmsTemplate jmsTemplate;
    private String queueName;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Trip(List<String> positions, String vehicleName, JmsTemplate jmsTemplate, String queueName) {
        this.positions = positions;
        this.vehicleName = vehicleName;
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    @Override
    public Object call() throws Exception {

        while (true) { // Infinitely churning out vehicles geo-coordinates to the queue
            positions.forEach(position -> {
                // To speed the vehicles up, we're going to drop some reports
                if (Math.random() < 0.5) return;

                // Since Position is in format of
                // lat="53.3798165805637836456298828125" lon="-1.46622528322041034698486328125"
                // we need to split the string
                String[] latAndLong = position.split("\""); // Splitting based on doubleQuotes(");

                /**
                 * Spring will convert {@link Map} to {@link javax.jms.MapMessage} using the default Message Converter
                 */
                Map<String, String> tripInformation = new HashMap<>();
                tripInformation.put("vehicle", vehicleName);
                tripInformation.put("lat", latAndLong[1]); // latitude
                tripInformation.put("long", latAndLong[3]); // longitude
                tripInformation.put("publishedTime", formatter.format(new Date()));

                // Publish the trip information to the Queue
                try {
                    sendTripInformationToQueue(tripInformation);
                    delay(Math.random() * 10000 + 2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Sends trip Information to the Queue {@link Trip#queueName}
     *
     * @param tripInformation
     */
    private void sendTripInformationToQueue(Map<String, String> tripInformation) throws InterruptedException {
        boolean messageNotSent = true;
        while (messageNotSent) {
            // broadcast trip details
            try {
                this.jmsTemplate.convertAndSend(queueName, tripInformation);
                TRIP_LOGGER.info("Sending Trip Details {} to the queue....", tripInformation);
                messageNotSent = false;
            } catch (UncategorizedJmsException ex) {
                // we are going to assume that this is due to downtime - back off and go again
                TRIP_LOGGER.error("Queue unavailable -- backing off for 5000ms before retry");
                delay(5000);
            }
        }
    }

    /**
     * Util method to introduce delay in sending information to queue, inorder to gracefully show the
     * movement of the trip.
     *
     * @param delayForDuration : Duration for which thread will go in sleep state.
     * @throws InterruptedException
     */
    private void delay(double delayForDuration) throws InterruptedException {
        TRIP_LOGGER.debug("Delaying for {} milliseconds ", delayForDuration);
        Thread.sleep((long) delayForDuration);
    }
}
