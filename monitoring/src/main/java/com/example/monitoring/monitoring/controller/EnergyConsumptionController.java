package com.example.monitoring.monitoring.controller;

import com.example.monitoring.monitoring.dto.EnergyConsumptionDTO;
import com.example.monitoring.monitoring.entity.Device;
import com.example.monitoring.monitoring.entity.HourlyEnergyConsumption;
import com.example.monitoring.monitoring.repository.DeviceRepository;
import com.example.monitoring.monitoring.repository.HourlyEnergyConsumptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/devices")
public class EnergyConsumptionController {

    @Autowired
    private HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/{deviceId}/energy-consumption")
    public ResponseEntity<?> getEnergyConsumption(
            @PathVariable UUID deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            System.out.println("DeviceId: " + deviceId);
            System.out.println("Date: " + date);

            Device device = deviceRepository.findByDeviceId(deviceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

            long startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            long endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

            System.out.println("Start of Day: " + startOfDay);
            System.out.println("End of Day: " + endOfDay);

            List<HourlyEnergyConsumption> records = hourlyEnergyConsumptionRepository
                    .findByDeviceIdAndTimestampBetween(device.getId(), startOfDay, endOfDay);

            System.out.println("Fetched records: " + records.size());


            if (records.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // Return an empty JSON array []
            }


            List<EnergyConsumptionDTO> response = records.stream()
                    .map(record -> new EnergyConsumptionDTO(record.getTimestamp(), record.getHourlyConsumption()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
