package com.example.fleet.model;

import java.util.Collections;
import java.util.Set;

public class Scooter extends Vehicle implements TwoWheeler {
    public Scooter() {}
    public Scooter(Long id, String make, String model, int year, String fuelType, Long ownerId) {
        super(id, make, model, year, fuelType, ownerId);
    }
    @Override
    public String getType() {
        return "Scooter";
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
        return false;
    }
    @Override
    public int maxPillionRiders() {
        return 0;
    }
    @Override
    public boolean supportsLongDistanceTravel() {
        return getYear() > 1998;
    }

}
