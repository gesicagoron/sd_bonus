package ro.tuc.ds2020.dtos;

import java.util.Objects;
import java.util.UUID;

public class PersonDTO {
    private UUID id;
    private String name;
    private int age;
    private String role;
    private String address;
    private String password;


    public PersonDTO() {
    }

    public PersonDTO(UUID id, String address, String name, int age, String role, String password) {
        this.id = id;
        this.address = address;
        this.name = name;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {  // Getter for address
        return address;
    }

    public void setAddress(String address) {  // Setter for address
        this.address = address;
    }

    public String getPassword() {  // Getter for password
        return password;
    }

    public void setPassword(String password) {  // Setter for password
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return age == personDTO.age &&
                Objects.equals(name, personDTO.name) &&
                Objects.equals(role, personDTO.role) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(password,personDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, role, address, password);
    }
}
