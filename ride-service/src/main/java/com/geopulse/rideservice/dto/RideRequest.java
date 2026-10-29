package com.geopulse.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @NotBlank(message = "Rider Id Is Required")
    private String riderId;

    @NotNull(message = "Pickup Latitude Is Required")
    private double pickupLatitude;

    @NotNull(message = "Pickup Longitude Is Required")
    private double pickupLongitude;

    @NotBlank(message = "Pickup Address Is Required")
    private String pickupAddress;

    @NotNull(message = "Drop Latitude Is Required")
    private double dropLatitude;

    @NotNull(message = "Drop Longitude Is Required")
    private double dropLongitude;

    @NotBlank(message = "Drop Address Is Required")
    private String dropAddress;
}
