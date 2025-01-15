package com.example.monitoring.monitoring.service;

import com.example.monitoring.monitoring.entity.Device;
import com.example.monitoring.monitoring.repository.DeviceRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceChangeListener {

    @Autowired
    private DeviceRepository deviceRepository;

    @RabbitListener(queues = "device_changes")
    public void handleDeviceChange(String message) {
        try {
            // Parse the message from RabbitMQ
            System.out.println("Received message from device_changes: " + message);
            JSONObject json = new JSONObject(message);
            String event = json.getString("event");
            UUID deviceId = UUID.fromString(json.getString("device_id"));
            Double maxHourlyEnergyConsumption = json.optDouble("max_hourly_energy_consumption", 0.0);

            switch (event.toLowerCase()) {
                case "create":
                    handleDeviceCreate(deviceId, maxHourlyEnergyConsumption);
                    break;

                case "update":
                    handleDeviceUpdate(deviceId, maxHourlyEnergyConsumption);
                    break;

                case "delete":
                    handleDeviceDelete(deviceId);
                    break;

                default:
                    System.err.println("Unrecognized event type: " + event);
            }
        } catch (Exception e) {
            System.err.println("Error processing device change message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeviceCreate(UUID deviceId, Double maxHourlyEnergyConsumption) {
        System.out.println("Handling device creation for device ID: " + deviceId);
        Device device = new Device(deviceId, maxHourlyEnergyConsumption);
        deviceRepository.save(device);
        System.out.println("Device created: " + deviceId);
    }

    private void handleDeviceUpdate(UUID deviceId, Double maxHourlyEnergyConsumption) {
        System.out.println("Handling device update for device ID: " + deviceId);
        Optional<Device> optionalDevice = deviceRepository.findByDeviceId(deviceId);

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            device.setMaxHourlyConsumption(maxHourlyEnergyConsumption);
            deviceRepository.save(device);
            System.out.println("Device updated: " + deviceId);
        } else {
            System.err.println("Device ID " + deviceId + " not found for update.");
        }
    }

    private void handleDeviceDelete(UUID deviceId) {
        System.out.println("Handling device deletion for device ID: " + deviceId);
        Optional<Device> optionalDevice = deviceRepository.findByDeviceId(deviceId);

        if (optionalDevice.isPresent()) {
            deviceRepository.delete(optionalDevice.get());
            System.out.println("Device deleted: " + deviceId);
        } else {
            System.err.println("Device ID " + deviceId + " not found for deletion.");
        }
    }
}
