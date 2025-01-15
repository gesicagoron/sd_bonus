package com.example.monitoring.monitoring.repository;

import com.example.monitoring.monitoring.entity.Device;
import com.example.monitoring.monitoring.entity.HourlyEnergyConsumption;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HourlyEnergyConsumptionRepository extends JpaRepository<HourlyEnergyConsumption, Long> {
    /**
     * Finds hourly energy consumption by device ID and timestamp.
     */
    @Query("SELECT h FROM HourlyEnergyConsumption h WHERE h.device = :device AND h.timestamp = :timestamp")
    Optional<HourlyEnergyConsumption> findByDeviceAndTimestamp(@Param("device") Device device, @Param("timestamp") long timestamp);

    /**
     * Deletes all hourly energy consumption records by device.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM HourlyEnergyConsumption h WHERE h.device = :device")
    void deleteAllByDevice(@Param("device") Device device);

    @Query("SELECT h FROM HourlyEnergyConsumption h WHERE h.device.id = :deviceId AND h.timestamp BETWEEN :start AND :end")
    List<HourlyEnergyConsumption> findByDeviceIdAndTimestampBetween(
            @Param("deviceId") Long deviceId,
            @Param("start") Long start,
            @Param("end") Long end);
}
