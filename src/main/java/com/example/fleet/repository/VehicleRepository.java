package com.example.fleet.repository;

import com.example.fleet.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    List<Vehicle> findAll();
    void deleteById(Long id);
    void deleteAll();
    List<Vehicle> findByFuelType(String fuelType);
    List<Vehicle> findByOwnerId(Long ownerId);
    List<Vehicle> findByNumberOfWheels(int numberOfWheels);
    List<Vehicle> findBySecurityMeasure(String securityMeasure);
}
