package com.example.fleet;

import com.example.fleet.model.Vehicle;
import com.example.fleet.repository.InMemoryVehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class FleetController {
    private final FleetManager fleetManager;

    public FleetController() {
        // For demo/test: use in-memory repository
        this.fleetManager = new FleetManager(new InMemoryVehicleRepository());
    }

    @PostMapping
    public ResponseEntity<String> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle saved = fleetManager.addVehicle(vehicle);
        return ResponseEntity.ok("Vehicle created with id: " + saved.getId());
    }

    @GetMapping("/describe")
    public ResponseEntity<List<String>> describeAllVehicles() {
        List<String> descriptions = fleetManager.getAllVehicles()
                .stream()
                .map(Vehicle::describe)
                .collect(Collectors.toList());
        return ResponseEntity.ok(descriptions);
    }

    @GetMapping("/describe/{id}")
    public ResponseEntity<String> describeVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = fleetManager.getVehicle(id);
        return vehicle
                .map(v -> ResponseEntity.ok(v.describe()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = fleetManager.getVehicle(id);
        return vehicle.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return fleetManager.getAllVehicles();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        fleetManager.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllVehicles() {
        fleetManager.deleteAllVehicles();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        try {
            return ResponseEntity.ok(fleetManager.updateVehicle(id, vehicle));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filter")
    public List<Vehicle> filterVehicles(
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Integer numberOfWheels,
            @RequestParam(required = false) String securityMeasure) {
        if (fuelType != null) return fleetManager.findByFuelType(fuelType);
        if (ownerId != null) return fleetManager.findByOwnerId(ownerId);
        if (numberOfWheels != null) return fleetManager.findByNumberOfWheels(numberOfWheels);
        if (securityMeasure != null) return fleetManager.findBySecurityMeasure(securityMeasure);
        return fleetManager.getAllVehicles();
    }
}
