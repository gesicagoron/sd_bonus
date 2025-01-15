package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.UserWithDevicesDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.UserRepository;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final DeviceRepository deviceRepository;

    @Autowired
    public UserService(UserRepository userRepository, DeviceService deviceService, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceService = deviceService;
        this.deviceRepository = deviceRepository;
    }

    public User findUserById(UUID userId) {
        System.out.println("Fetching user with ID: " + userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public void createUserFromPerson(PersonDTO personDTO) {
        User newUser = new User(personDTO.getId(), personDTO.getName());
        userRepository.save(newUser);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void assignDeviceToUser(UUID userId, UUID deviceId) {
        deviceService.assignDeviceToUser(userId, deviceId);
    }

    public boolean userExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserWithDevicesDTO> findAllUsersWithDevices() {
        List<User> users = userRepository.findAllWithDevices();

        return users.stream().map(user -> {
            List<DeviceDTO> deviceDTOs = user.getDevices().stream()
                    .map(device -> new DeviceDTO(
                            device.getId(),
                            device.getDescription(),
                            device.getAddress(),
                            device.getMaxHourlyEnergyConsumption(),
                            device.getUserId()
                    )).collect(Collectors.toList());

            return new UserWithDevicesDTO(user.getId(), user.getName(), deviceDTOs);
        }).collect(Collectors.toList());
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }


}
