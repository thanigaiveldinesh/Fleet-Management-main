package com.example.fleet.model;

import java.util.Collections;
import java.util.Set;

public class Bus extends Vehicle implements FourWheeler {
    public Bus() {}
    public Bus(Long id, String make, String model, int year, String fuelType, Long ownerId) {
        super(id, make, model, year, fuelType, ownerId);
    }
    @Override
    public String getType() {
        return "Bus";
    }

    @Override
    public String describe() {
        return String.format(
                "Vehicle [ID: %d, Make: %s, Model: %s, Year: %d, Spare Wheels: %d]",
                getId(), getMake(), getModel(), getYear(), getSpareTireCount()
        );
    }
    @Override
    public Set<SecurityMeasure> getRequiredSecurityMeasures() {
        return Set.of(SecurityMeasure.NONE);
    }
    @Override
    public int getNumberOfWheels() {
        return FourWheeler.super.getNumberOfWheels();
    }
    @Override
    public int getSpareTireCount() {
        return getYear() < 1990 ? 3 : 2;
    }
    @Override
    public double getMaxCargoCapacityInTons() {
        return 30;
    }

}
