package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.LoginRequestDTO;
import ro.tuc.ds2020.dtos.LoginResponseDTO;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;
import ro.tuc.ds2020.security.JWTGenerator;

import java.util.Optional;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final PersonRepository personRepository;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, JWTGenerator jwtGenerator, PersonRepository personRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.personRepository = personRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getName(),
                        loginRequestDTO.getPassword()
                )
        );

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtGenerator.generateToken(authentication);

        // Fetch role
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER");

        // Fetch user ID from the database using the username
        Optional<Person> user = personRepository.findByName(loginRequestDTO.getName());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found: " + loginRequestDTO.getName());
        }

        // Return LoginResponseDTO with token, role, and userId
        return new LoginResponseDTO(token, role, user.get().getId().toString());
    }
}
