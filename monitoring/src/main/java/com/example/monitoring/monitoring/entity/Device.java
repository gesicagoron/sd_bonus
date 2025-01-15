package com.example.monitoring.monitoring.entity;

import com.example.monitoring.monitoring.entity.HourlyEnergyConsumption;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID deviceId; // Change to UUID

    @Column(nullable = false)
    private Double maxHourlyConsumption;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HourlyEnergyConsumption> hourlyEnergyConsumptions;

    // Constructors
    public Device() {}

    public Device(UUID deviceId, Double maxHourlyConsumption) {
        this.deviceId = deviceId;
        this.maxHourlyConsumption = maxHourlyConsumption;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public Double getMaxHourlyConsumption() {
        return maxHourlyConsumption;
    }

    public void setMaxHourlyConsumption(Double maxHourlyConsumption) {
        this.maxHourlyConsumption = maxHourlyConsumption;
    }

    public List<HourlyEnergyConsumption> getHourlyEnergyConsumptions() {
        return hourlyEnergyConsumptions;
    }

    public void setHourlyEnergyConsumptions(List<HourlyEnergyConsumption> hourlyEnergyConsumptions) {
        this.hourlyEnergyConsumptions = hourlyEnergyConsumptions;
    }
}
