package com.example.fleet.model;

import java.util.Collections;
import java.util.Set;

public class Truck extends Vehicle implements FourWheeler {
    public Truck() {}
    public Truck(Long id, String make, String model, int year, String fuelType, Long ownerId) {
        super(id, make, model, year, fuelType, ownerId);
    }
    @Override
    public String getType() {
        return "Truck";
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
        if (getYear() > 2007) {
            return Set.of(SecurityMeasure.SEAT_BELT, SecurityMeasure.AIRBAG);
        } else {
            return Set.of(SecurityMeasure.SEAT_BELT);
        }
    }
    @Override
    public int getNumberOfWheels() {
        return FourWheeler.super.getNumberOfWheels();
    }
    @Override
    public int getSpareTireCount() {
        return 1;
    }
    @Override
    public double getMaxCargoCapacityInTons() {
        return 5;
    }

}
