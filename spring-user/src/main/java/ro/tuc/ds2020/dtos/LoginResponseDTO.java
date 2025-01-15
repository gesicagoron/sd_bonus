package ro.tuc.ds2020.dtos;

public class LoginResponseDTO {
    private String token;
    private String role;
    private String userId; // Add userId to the response

    // Constructors
    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String role, String userId) {
        this.token = token;
        this.role = role;
        this.userId = userId; // Initialize userId
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() { // Getter for userId
        return userId;
    }

    public void setUserId(String userId) { // Setter for userId
        this.userId = userId;
    }
}
