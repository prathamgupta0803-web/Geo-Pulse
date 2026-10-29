package com.geopulse.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Receive Response From Location Service
// When Querying For NearBy Drivers

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String  driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
