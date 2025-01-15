package ro.tuc.ds2020.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public MyUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Person person = personRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + name));

        return User.builder()
                .username(person.getName())
                .password(person.getPassword())
                .roles(person.getRole())
                .build();
    }
}
