package com.example.fleet;

import com.example.fleet.model.Vehicle;
import com.example.fleet.repository.VehicleRepository;
import java.util.List;
import java.util.Optional;

public class FleetManager {
    private final VehicleRepository vehicleRepository;

    public FleetManager(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> getVehicle(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public void deleteAllVehicles() {
        vehicleRepository.deleteAll();
    }

    public List<Vehicle> findByFuelType(String fuelType) {
        return vehicleRepository.findByFuelType(fuelType);
    }

    public List<Vehicle> findByOwnerId(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId);
    }

    public List<Vehicle> findByNumberOfWheels(int numberOfWheels) {
        return vehicleRepository.findByNumberOfWheels(numberOfWheels);
    }

    public List<Vehicle> findBySecurityMeasure(String securityMeasure) {
        return vehicleRepository.findBySecurityMeasure(securityMeasure);
    }

    public Vehicle updateVehicle(Long id, Vehicle updated) {
        Optional<Vehicle> existing = vehicleRepository.findById(id);
        if (existing.isPresent()) {
            updated.setId(id);
            return vehicleRepository.save(updated);
        } else {
            throw new IllegalArgumentException("Vehicle not found");
        }
    }
}
