package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.services.PersonService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/person")
public class PersonController {

    private final PersonService personService;

    private final RestTemplate restTemplate;
    private final String deviceServiceUrl = "http://reverse-proxy/device/users";

    @Autowired
    public PersonController(PersonService personService, RestTemplate restTemplate) {
        this.personService = personService;
        this.restTemplate = restTemplate;
    }

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> dtos = personService.findPersons();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertProsumer(@Valid @RequestBody PersonDetailsDTO personDTO) {
        // Save the person in the Person microservice
        UUID personID = personService.insert(personDTO);

        // Synchronously notify the Device microservice
        PersonDTO createdPerson = personService.findPersonById(personID);
        String deviceServiceUrl = "http://reverse-proxy/device/users";
        restTemplate.postForEntity(deviceServiceUrl, createdPerson, Void.class);

        return new ResponseEntity<>(personID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable("id") UUID personId) {
        PersonDTO dto = personService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable UUID id, @Valid @RequestBody PersonDetailsDTO personDTO) {
        PersonDTO updatedPerson = personService.updatePerson(id, personDTO);
        return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        personService.deletePerson(id);

        try {
            restTemplate.delete(deviceServiceUrl + "/" + id);
        } catch (Exception e) {
            System.err.println("Error deleting user in Device microservice: " + e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
