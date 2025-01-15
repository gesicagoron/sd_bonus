package com.example.monitoring.monitoring.repository;

import com.example.monitoring.monitoring.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT d FROM Device d WHERE d.deviceId = :deviceId")
    Optional<Device> findByDeviceId(@Param("deviceId") UUID deviceId);

}

