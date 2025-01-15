package com.example.monitoring.monitoring.dto;

public class EnergyConsumptionDTO {
    private long timestamp; // Epoch time
    private Double energyValue;

    public EnergyConsumptionDTO(long timestamp, Double energyValue) {
        this.timestamp = timestamp;
        this.energyValue = energyValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(Double energyValue) {
        this.energyValue = energyValue;
    }
}
