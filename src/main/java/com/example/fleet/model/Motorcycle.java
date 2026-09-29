package com.example.fleet.model;

import java.util.Collections;
import java.util.Set;

public class Motorcycle extends Vehicle implements TwoWheeler {
    public Motorcycle() {}
    public Motorcycle(Long id, String make, String model, int year, String fuelType, Long ownerId) {
        super(id, make, model, year, fuelType, ownerId);
    }
    @Override
    public String getType() {
        return "Motorcycle";
    }

    @Override
    public String describe() {
        return String.format(
                "Vehicle [ID: %d, Make: %s, Model: %s, Year: %d, Pillion Rider: %d]",
                getId(), getMake(), getModel(), getYear(), maxPillionRiders()
        );
    }
    @Override
    public Set<SecurityMeasure> getRequiredSecurityMeasures() {
        return Collections.singleton(SecurityMeasure.HELMET);
    }
    @Override
    public int getNumberOfWheels() {
        return TwoWheeler.super.getNumberOfWheels();
    }
    @Override
    public boolean isSidecarCompatible() {
        return getYear() > 1995;
    }
    @Override
    public int maxPillionRiders() {
        return 1;
    }
    @Override
    public boolean supportsLongDistanceTravel() {
        return true;
    }

}
