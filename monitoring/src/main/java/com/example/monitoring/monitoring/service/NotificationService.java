package com.example.monitoring.monitoring.service;

import com.example.monitoring.monitoring.config.NotificationWebSocketHandler;
import com.example.monitoring.monitoring.entity.Device;
import com.example.monitoring.monitoring.entity.HourlyEnergyConsumption;
import com.example.monitoring.monitoring.repository.DeviceRepository;
import com.example.monitoring.monitoring.repository.HourlyEnergyConsumptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    @Autowired
    private DeviceRepository deviceRepository; // Add this line

    @Autowired
    private DeviceServiceClient deviceServiceClient;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    public void checkAndNotify(UUID deviceId, long timestamp) {
        System.out.println("DEBUG: checkAndNotify called for deviceId: " + deviceId + ", timestamp: " + timestamp);

        try {
            // Retrieve the Device entity by deviceId
            Optional<Device> optionalDevice = deviceRepository.findByDeviceId(deviceId);

            if (optionalDevice.isEmpty()) {
                System.out.println("DEBUG: Device not found for deviceId: " + deviceId);
                return;
            }

            Device device = optionalDevice.get();

            // Retrieve the hourly consumption for the given Device and timestamp
            Optional<HourlyEnergyConsumption> optionalConsumption =
                    hourlyEnergyConsumptionRepository.findByDeviceAndTimestamp(device, timestamp);

            if (optionalConsumption.isEmpty()) {
                System.out.println("DEBUG: No consumption found for device: " + deviceId + " at timestamp: " + timestamp);
                return;
            }

            HourlyEnergyConsumption consumption = optionalConsumption.get();

            // Retrieve the maximum hourly consumption for the device
            Double maxHourlyConsumption = device.getMaxHourlyConsumption();
            System.out.println("DEBUG: Found consumption: " + consumption.getHourlyConsumption() + ", Max: " + maxHourlyConsumption);

            // Check if the consumption exceeds the max allowed
            if (consumption.getHourlyConsumption() > maxHourlyConsumption) {
                String notification = String.format(
                        "Device %s exceeded the maximum hourly energy consumption! Current: %.2f, Max: %.2f",
                        deviceId, consumption.getHourlyConsumption(), maxHourlyConsumption
                );

                System.out.println("DEBUG: Preparing to send notification: " + notification);

                // Send the notification via WebSocket
                webSocketHandler.sendNotification(notification);
            } else {
                System.out.println("DEBUG: Consumption is within limits for device: " + deviceId);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Invalid deviceId format: " + deviceId + ". Exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: An error occurred while processing notification for deviceId: " + deviceId + ". Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
