package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.dtos.validators.annotation.AgeLimit;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PersonDetailsDTO {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String address;
    @AgeLimit(limit = 18)
    private int age;
    @NotNull
    private String role;
    @NotNull
    private String password;


    public PersonDetailsDTO() {
    }

    public PersonDetailsDTO( String name, String address, int age, String role, String password ) {
        this.name = name;
        this.address = address;
        this.age = age;
        this.role = role;
        this.password=password;
    }

    public PersonDetailsDTO(UUID id, String name, String address, int age, String role, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.role = role;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {return role;}

    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

}
