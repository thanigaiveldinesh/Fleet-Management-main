package com.example.fleet.repository;

import com.example.fleet.model.Vehicle;
import com.example.fleet.model.SecurityMeasure;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<Long, Vehicle> vehicles = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(idGenerator.getAndIncrement());
        }
        vehicles.put(idGenerator.getAndIncrement(), vehicle);
        return vehicle;
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles.values());
    }

    @Override
    public void deleteById(Long id) {
        vehicles.remove(id);
    }

    @Override
    public List<Vehicle> findByFuelType(String fuelType) {
        return vehicles.values().stream()
                .filter(v -> v.getFuelType() != null && v.getFuelType().equalsIgnoreCase(fuelType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> findByOwnerId(Long ownerId) {
        return vehicles.values().stream()
                .filter(v -> v.getOwnerId() != null && v.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> findByNumberOfWheels(int numberOfWheels) {
        return vehicles.values().stream()
            .filter(v -> v.getNumberOfWheels() == numberOfWheels)
            .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> findBySecurityMeasure(String securityMeasure) {
        return vehicles.values().stream()
                .filter(v -> v.getRequiredSecurityMeasures().stream()
                        .anyMatch(sm -> sm.name().equalsIgnoreCase(securityMeasure)))
                .collect(Collectors.toList());
    }

    public Vehicle saveCorrectly(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(idGenerator.getAndIncrement());
        }
        vehicles.put(vehicle.getId(), vehicle);
        return vehicle;
    }

    @Override
    public void deleteAll() {
        vehicles.clear();
    }
}
