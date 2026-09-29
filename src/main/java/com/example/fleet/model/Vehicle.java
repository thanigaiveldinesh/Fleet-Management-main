package com.example.fleet.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Car.class, name = "Car"),
    @JsonSubTypes.Type(value = Truck.class, name = "Truck"),
    @JsonSubTypes.Type(value = Bus.class, name = "Bus"),
    @JsonSubTypes.Type(value = Motorcycle.class, name = "Motorcycle"),
    @JsonSubTypes.Type(value = Scooter.class, name = "Scooter")
})
public abstract class Vehicle {
    private Long id;
    private String make;
    private String model;
    private int year;
    private String fuelType;
    private Long ownerId;

    public Vehicle() {}

    public Vehicle(Long id, String make, String model, int year, String fuelType, Long ownerId) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.fuelType = fuelType;
        this.ownerId = ownerId;
    }

    public abstract String getType();
    public abstract String describe();
    public abstract Set<SecurityMeasure> getRequiredSecurityMeasures();
    public abstract int getNumberOfWheels();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
