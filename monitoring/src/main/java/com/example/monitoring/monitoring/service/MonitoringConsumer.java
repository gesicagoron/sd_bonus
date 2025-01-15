package com.example.monitoring.monitoring.service;

import com.example.monitoring.monitoring.entity.Device;
import com.example.monitoring.monitoring.entity.HourlyEnergyConsumption;
import com.example.monitoring.monitoring.repository.DeviceRepository;
import com.example.monitoring.monitoring.repository.HourlyEnergyConsumptionRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoringConsumer {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = "energy_measurements")
    public void receiveMessage(String message) {
        try {
            // Parse the message
            System.out.println("Received message: " + message);
            JSONObject json = new JSONObject(message);
            UUID deviceId = UUID.fromString(json.getString("device_id").trim());
            long timestamp = json.getLong("timestamp");
            double measurementValue = json.getDouble("measurement_value");

            // Debug all devices in the database
            List<Device> devices = deviceRepository.findAll();
            devices.forEach(device -> System.out.println("DEBUG: Device in DB -> " + device.getDeviceId()));

            // Validate the device
            Optional<Device> optionalDevice = deviceRepository.findByDeviceId(deviceId);
            if (optionalDevice.isEmpty()) {
                System.err.println("Device ID " + deviceId + " not found in the database.");
                return;
            }

            Device device = optionalDevice.get();

            // Compute the hourly timestamp
            ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC);
            long hourTimestamp = zdt.withMinute(0).withSecond(0).withNano(0).toInstant().toEpochMilli();

            // Update or create a new hourly energy consumption record
            HourlyEnergyConsumption hourlyConsumption = hourlyEnergyConsumptionRepository
                    .findByDeviceAndTimestamp(device, hourTimestamp)
                    .orElse(new HourlyEnergyConsumption(0.0, hourTimestamp, device));

            hourlyConsumption.setHourlyConsumption(hourlyConsumption.getHourlyConsumption() + measurementValue);
            hourlyEnergyConsumptionRepository.save(hourlyConsumption);

            // Notify if the hourly consumption exceeds the device's maximum limit
            if (hourlyConsumption.getHourlyConsumption() > device.getMaxHourlyConsumption()) {
                System.out.println("WARNING: Device " + deviceId + " exceeded maximum hourly consumption!");

                // Call checkAndNotify to handle WebSocket notifications
                notificationService.checkAndNotify(deviceId, hourTimestamp);
            }
        } catch (Exception e) {
            System.err.println("Error parsing or processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
