package ro.tuc.ds2020.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Device;

@Service
public class DeviceEventPublisher {

    private static final String DEVICE_CHANGES_QUEUE = "device_changes";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishDeviceChange(String eventType, Device device) {
        String message = String.format(
                "{\"event\": \"%s\", \"device_id\": \"%s\", \"description\": \"%s\", \"address\": \"%s\", \"max_hourly_energy_consumption\": %.2f}",
                eventType, device.getId(), device.getDescription(), device.getAddress(), device.getMaxHourlyEnergyConsumption()
        );
        rabbitTemplate.convertAndSend(DEVICE_CHANGES_QUEUE, message);
        System.out.println("Published device change: " + message);
    }
}
