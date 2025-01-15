package ro.tuc.ds2020.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final PersonServiceClient personServiceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, PersonServiceClient personServiceClient) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.personServiceClient = personServiceClient;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO findDeviceById(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return DeviceBuilder.toDeviceDTO(device);
    }

    public UUID insert(DeviceDetailsDTO deviceDetailsDTO) {
        Device device = DeviceBuilder.toEntity(deviceDetailsDTO);
        device = deviceRepository.save(device);

        // Publish device creation event to RabbitMQ
        publishDeviceChange("create", device);

        return device.getId();
    }

    public DeviceDTO updateDevice(UUID id, DeviceDetailsDTO deviceDetailsDTO) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        device.setDescription(deviceDetailsDTO.getDescription());
        device.setAddress(deviceDetailsDTO.getAddress());
        device.setMaxHourlyEnergyConsumption(deviceDetailsDTO.getMaxHourlyEnergyConsumption());
        device = deviceRepository.save(device);

        // Publish device update to RabbitMQ
        publishDeviceChange("update", device);

        return DeviceBuilder.toDeviceDTO(device);
    }


    public void deleteDevice(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        deviceRepository.delete(device);

        // Publish device deletion event to RabbitMQ
        publishDeviceChange("delete", device);
    }

    public void assignDeviceToUser(UUID userId, UUID deviceId) {
        // Attempt to fetch the user locally in the `User` table
        Optional<User> existingUserOpt = userRepository.findById(userId);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
        } else {

            PersonDTO personDTO = personServiceClient.getPersonById(userId);

            if (personDTO == null) {
                throw new ResourceNotFoundException("User not found in Person microservice with id: " + userId);
            }

            user = new User(personDTO.getId(), personDTO.getName());
            userRepository.save(user);
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        device.setUser(user);
        deviceRepository.save(device);

        // Publish device assignment event to RabbitMQ
        publishDeviceChange("assign", device);
    }

    public User findUserById(UUID userId) {
        System.out.println("Fetching user with ID: " + userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public List<DeviceDTO> getDevicesByUserId(UUID userId) {
        List<Device> devices = deviceRepository.findByUser_Id(userId);
        return devices.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }


    // Publish message to device_changes queue
//    public void publishDeviceChange(String action, UUID deviceId) {
//        String message = String.format("{\"action\": \"%s\", \"device_id\": \"%s\"}", action, deviceId);
//        rabbitTemplate.convertAndSend("device_changes", message);
//        System.out.println("Published message to device_changes: " + message);
//    }
    public void publishDeviceChange(String eventType, Device device) {
        String message = String.format(
                "{\"event\": \"%s\", \"device_id\": \"%s\", \"max_hourly_energy_consumption\": %.2f}",
                eventType, device.getId(), device.getMaxHourlyEnergyConsumption()
        );
        rabbitTemplate.convertAndSend("device_changes", message);
        System.out.println("Published device change: " + message);
    }
}
