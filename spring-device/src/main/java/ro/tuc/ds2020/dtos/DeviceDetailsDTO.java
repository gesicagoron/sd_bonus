package ro.tuc.ds2020.dtos;

import javax.validation.constraints.NotNull;

public class DeviceDetailsDTO {

    @NotNull
    private String description;

    @NotNull
    private String address;

    @NotNull
    private Double maxHourlyEnergyConsumption;

    public DeviceDetailsDTO() {}

    public DeviceDetailsDTO(String description, String address, Double maxHourlyEnergyConsumption) {
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(Double maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }


    @Override
    public String toString() {
        return "DeviceDetailsDTO{" +
                "description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", maxHourlyEnergyConsumption=" + maxHourlyEnergyConsumption +
                '}';
    }
}
