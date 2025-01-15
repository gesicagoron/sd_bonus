package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.PersonDTO;

import java.util.UUID;

@Service
public class PersonServiceClient {

    private final RestTemplate restTemplate;
    private final String personServiceUrl = "http://reverse-proxy/person";
    ;
    @Autowired
    public PersonServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PersonDTO createPerson(PersonDTO personDTO) {

        return restTemplate.postForObject(personServiceUrl, personDTO, PersonDTO.class);
    }

    public PersonDTO getPersonById(UUID personId) {
        return restTemplate.getForObject(personServiceUrl + "/" + personId, PersonDTO.class);
    }
}
