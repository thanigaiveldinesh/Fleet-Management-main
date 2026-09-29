package com.example.fleet;

import com.example.fleet.model.Car;
import com.example.fleet.model.Vehicle;
import com.example.fleet.repository.InMemoryVehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class FleetApplicationTests {
    private FleetManager fleetManager;

    @BeforeEach
    void setUp() {
        fleetManager = new FleetManager(new InMemoryVehicleRepository());
    }

    //@Test
    void testAddAndGetVehicle() {
        Vehicle car = new Car(null, "Toyota", "Corolla", 2020, "Petrol", 1L);
        Vehicle saved = fleetManager.addVehicle(car);
        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("Toyota", saved.getMake());
        Assertions.assertTrue(fleetManager.getVehicle(saved.getId()).isPresent());
    }

    @Test
    void testDeleteVehicle() {
        Vehicle car = new Car(null, "Honda", "Civic", 2019, "Diesel", 2L);
        Vehicle saved = fleetManager.addVehicle(car);
        fleetManager.deleteVehicle(saved.getId());
        Assertions.assertTrue(fleetManager.getVehicle(saved.getId()).isEmpty());
    }

    @Test
    void testFilterByFuelType() {
        fleetManager.addVehicle(new Car(null, "Ford", "Focus", 2018, "Petrol", 3L));
        fleetManager.addVehicle(new Car(null, "VW", "Golf", 2017, "Diesel", 4L));
        List<Vehicle> petrolCars = fleetManager.findByFuelType("Petrol");
        Assertions.assertFalse(petrolCars.isEmpty());
        Assertions.assertTrue(petrolCars.stream().allMatch(v -> v.getFuelType().equalsIgnoreCase("Petrol")));
    }
}

