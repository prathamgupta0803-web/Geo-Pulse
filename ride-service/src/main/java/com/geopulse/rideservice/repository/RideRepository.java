package com.geopulse.rideservice.repository;

import com.geopulse.rideservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride,String> {
    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);
}
