package ro.tuc.ds2020.dtos;

public class LoginRequestDTO {
    private String name;
    private String password;

    // Constructors
    public LoginRequestDTO() {}

    public LoginRequestDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
