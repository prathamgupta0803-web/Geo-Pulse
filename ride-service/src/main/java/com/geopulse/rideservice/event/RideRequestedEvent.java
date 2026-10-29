package com.geopulse.rideservice.event;


/*
* Event Publish to Kafka When a Ride is Requested
* Matching Service will Consume this Event
* Topic : ride.requested
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
