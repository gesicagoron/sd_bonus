package ro.tuc.ds2020.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class User {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices;

    public User() {}


    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }


    public User(String name) {
        this.name = name;
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

    public List<Device> getDevices() {
        return devices;
    }
}
