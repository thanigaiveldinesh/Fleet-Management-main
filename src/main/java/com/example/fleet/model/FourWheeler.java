package com.example.fleet.model;

public interface FourWheeler {
    default int getNumberOfWheels() {
        return 4;
    }
    int getSpareTireCount();
    double getMaxCargoCapacityInTons();
}
