package com.geopulse.locationservice.service;

import com.geopulse.locationservice.dto.DriverLocationRequest;
import com.geopulse.locationservice.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisGeoCommands;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String,String> redisTemplate;
    // redis key for all driver location
    private static final String DRIVERS_GEO_KEY = "driver : location";

    /**
     * Update Driver Location in redis
     * Called in every 3 sec by driver's phone
     * Map to redis GEOADD command
    */
    public void updateDriverLocation(DriverLocationRequest locationRequest) {
        log.info("Updating Location For Driver: {}", locationRequest.getDriverId());

        Point point = new Point(
                locationRequest.getLongitude(),
                locationRequest.getLatitude()
        );
        // opsForGeo() : Method gives us access to all the Geo Spatial Commands
        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY,point, locationRequest.getDriverId());

        log.info("Updated Location For Driver: {}", locationRequest.getDriverId());
    }

    /**
     * find nearby driver within given radius
     * called by matching service on ride request
     * map to FEORADIUS command
     */
    public List<NearByDriverResponse> findNearbyDrivers(
            double latitude, double longitude, double radiusInKm) {

        log.info("Finding drivers near lat: {} long: {} withing {}Km",
                latitude, longitude, radiusInKm);

        // Defining Search Area
        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS)
        );

        // Searching Driver in rider's radius
        // Returning collection of GeoResult
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10)
                );
        List<NearByDriverResponse> nearbyDrivers = new ArrayList<>();

        // Adding Drivers to result list
        if(results != null){
            results.getContent().forEach(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearbyDrivers.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }
        log.info("Found {} drivers nearby", nearbyDrivers.size());
        return nearbyDrivers;
    }

    /**
     * Remove Driver When they go Offline
     * Map to Redis ZREM Command
     */
    public void removeDriver(String driverId) {
        log.info("Removing Driver : {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY,driverId);
        log.info("Driver Removed Successfully");
    }
}
