package com.geopulse.matchingservice.service;

import com.geopulse.matchingservice.client.LocationServiceClient;
import com.geopulse.matchingservice.dto.NearByDriverResponse;
import com.geopulse.matchingservice.event.RideMatchedEvent;
import com.geopulse.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;
    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;

    /**
     * Main Matching Algorithm
     * It is called when Ride Request Event is Consumed by Kafka
     * @Param event
     *
     * Steps:
     * 1. Ask Location Service For Nearby Drivers
     * 2. Score each Driver and Pick the Best Driver
     * 3. Publish the Matched Event to Kafka
     */
    public void matchDriverForRide(RideRequestedEvent event) {
        List<NearByDriverResponse> drivers = locationServiceClient.getNearbyDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_KM
        );
        if(drivers.isEmpty()) {
            log.warn("No Driver Found Near Rider's Location");
            return;
        }

        Optional<NearByDriverResponse> bestDriver = findBestDriver(drivers);
        if(bestDriver.isEmpty()) {
            log.warn("Could not Find Suitable Driver for the Ride");
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        // Publish Ride Match Event To Kafka
        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC,event.getRideId(),matchedEvent);
        log.info("Ride Matched Event Publish");
    }

    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers) {
        // Score: 70% weightage => Distance || 30% Weightage => Rating
        // Score = (1/distance) * distanceWeight + rating * ratingWeight
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;
        return drivers.stream().max(
                Comparator.comparingDouble(driver -> {
                    // Adding 0.1 to avoid division by 0 if in case distance = 0
                    double distanceScore = (1.0/(driver.getDistanceInKm()+0.1)) * distanceWeight;

                    // Simulated Rating 4-5
                    // In production fetch from Driver Service
                    double ratingScore = (4.0 + Math.random()) * ratingWeight;
                    return distanceScore + ratingScore;
                })
        );
    }
}
