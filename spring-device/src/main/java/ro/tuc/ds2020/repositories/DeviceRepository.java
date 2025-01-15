package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generated Query by Field
     * Find devices by description
     */
    List<Device> findByDescription(String description);

    /**
     * Example: Write Custom Query
     * Find devices with maximum hourly energy consumption greater than a specified value
     */
    @Query(value = "SELECT d " +
            "FROM Device d " +
            "WHERE d.maxHourlyEnergyConsumption > :energyConsumption")
    List<Device> findDevicesByMaxEnergyConsumptionGreaterThan(@Param("energyConsumption") Double energyConsumption);

    List<Device> findByUser_Id(UUID userId);

}
