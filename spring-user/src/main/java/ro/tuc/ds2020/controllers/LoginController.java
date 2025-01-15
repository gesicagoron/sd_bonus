package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.LoginRequestDTO;
import ro.tuc.ds2020.dtos.LoginResponseDTO;
import ro.tuc.ds2020.services.LoginService;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = loginService.login(loginRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
