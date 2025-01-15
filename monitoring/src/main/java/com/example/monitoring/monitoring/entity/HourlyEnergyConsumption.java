package com.example.monitoring.monitoring.entity;

import jakarta.persistence.*;

@Entity
public class HourlyEnergyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double hourlyConsumption;

    @Column(nullable = false)
    private Long timestamp;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // Constructors
    public HourlyEnergyConsumption() {}

    public HourlyEnergyConsumption(Double hourlyConsumption, Long timestamp, Device device) {
        this.hourlyConsumption = hourlyConsumption;
        this.timestamp = timestamp;
        this.device = device;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getHourlyConsumption() {
        return hourlyConsumption;
    }

    public void setHourlyConsumption(Double hourlyConsumption) {
        this.hourlyConsumption = hourlyConsumption;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
