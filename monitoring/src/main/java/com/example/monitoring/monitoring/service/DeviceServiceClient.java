package com.example.monitoring.monitoring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceServiceClient {

    // URL to access the device microservice
    private static final String DEVICE_SERVICE_URL = "http://device.localhost/devices";

    @Autowired
    private RestTemplate restTemplate;

    // Fetch all devices and return as a list of IDs
    public List<UUID> getAllDeviceIds() {
        JsonNode devices = restTemplate.getForObject(DEVICE_SERVICE_URL, JsonNode.class);

        if (devices == null || !devices.isArray()) {
            throw new IllegalStateException("Failed to fetch devices or response is invalid");
        }

        // Extract and return device IDs
        return devices.findValuesAsText("id").stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    // Fetch a specific device by its ID as raw JSON
    public JsonNode getDeviceById(UUID deviceId) {
        String url = DEVICE_SERVICE_URL + "/" + deviceId;
        return restTemplate.getForObject(url, JsonNode.class);
    }

    public Double getDeviceMaxHourlyConsumption(UUID deviceId) {
        String url = DEVICE_SERVICE_URL + "/" + deviceId;
        JsonNode deviceData = restTemplate.getForObject(url, JsonNode.class);

        if (deviceData != null && deviceData.has("maxHourlyEnergyConsumption")) {
            return deviceData.get("maxHourlyEnergyConsumption").asDouble();
        } else {
            throw new IllegalStateException("Device data not found or invalid for deviceId: " + deviceId);
        }
    }
}
