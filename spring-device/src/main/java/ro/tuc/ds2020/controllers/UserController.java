package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.UserWithDevicesDTO;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.services.UserService;
import ro.tuc.ds2020.services.DeviceService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final DeviceService deviceService;

    @Autowired
    public UserController(UserService userService, DeviceService deviceService) {
        this.userService = userService;
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<Void> createUserFromPerson(@RequestBody PersonDTO personDTO) {
        if (!userService.userExists(personDTO.getId())) {
            User newUser = new User(personDTO.getId(), personDTO.getName());
            userService.saveUser(newUser);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<PersonDTO> userDTOs = users.stream()
                .map(user -> new PersonDTO(user.getId(), user.getName()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/assigned")
    public ResponseEntity<?> getAllUsersWithDevices() {
        try {
            List<UserWithDevicesDTO> userDevices = userService.findAllUsersWithDevices();
            return new ResponseEntity<>(userDevices, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/devices/{userId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUserId(@PathVariable UUID userId) {
        List<DeviceDTO> devices = deviceService.getDevicesByUserId(userId);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insertUser(@RequestBody PersonDTO personDTO) {
        User newUser = new User(personDTO.getName());
        userService.saveUser(newUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
