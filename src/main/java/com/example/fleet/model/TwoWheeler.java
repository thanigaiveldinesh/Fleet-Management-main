package com.example.fleet.model;

public interface TwoWheeler {
    default int getNumberOfWheels() {
        return 2;
    }

    boolean isSidecarCompatible();
    int maxPillionRiders();
    boolean supportsLongDistanceTravel();
}
