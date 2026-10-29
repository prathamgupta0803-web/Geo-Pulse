package com.geopulse.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// Event Consumed from Kafka Topic : ride.requested
// Published by Ride Service when a User Request a Ride
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {
    private String rideId;
    private String riderId;

    // PICKUP
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    // DROP
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}
