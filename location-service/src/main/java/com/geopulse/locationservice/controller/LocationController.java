package com.geopulse.locationservice.controller;

import com.geopulse.locationservice.dto.DriverLocationRequest;
import com.geopulse.locationservice.dto.NearByDriverResponse;
import com.geopulse.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    private final LocationService locationService;

    // driver phone call this in every 3 sec for location update
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(
            @RequestBody DriverLocationRequest locationRequest) {
        locationService.updateDriverLocation(locationRequest);
        return ResponseEntity.ok("Driver's Location Updated Successfully");
    }

    // matching service call this function when ride is requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearbyDrivers(@RequestParam double latitude,
                                                                       @RequestParam double longitude,
                                                                       @RequestParam (defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(locationService.findNearbyDrivers(latitude,longitude,radius));
    }

    // Called when driver goes offline
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver Removed Successfully");
    }
}
