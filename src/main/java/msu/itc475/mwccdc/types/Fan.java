package msu.itc475.mwccdc.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Fan {

    @Id
    @Column(name = "fan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("firstName") // Add @JsonProperty for firstName
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty("lastName") // Add @JsonProperty for lastName
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty("email") // Add @JsonProperty for email
    private String email;

    @JsonProperty("phone") // Add @JsonProperty for phone
    private String phone;

    @JsonProperty("firstOccupation") // Add @JsonProperty for firstOccupation
    @Column(name = "first_occupation")
    private String firstOccupation;

    @JsonProperty("preferredStand") // Add @JsonProperty for preferredStand
    @Column(name = "preferred_stand")
    private String preferredStand;

    @JsonProperty("reservationTime") // Add @JsonProperty for reservationTime
    @Column(name = "reservation_time")
    private String reservationTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstOccupation() {
        return firstOccupation;
    }

    public void setFirstOccupation(String firstOccupation) {
        this.firstOccupation = firstOccupation;
    }

    public String getPreferredStand() {
        return preferredStand;
    }

    public void setPreferredStand(String preferredStand) {
        this.preferredStand = preferredStand;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public boolean isMilitary() {
        return "MLT".equalsIgnoreCase(firstOccupation);
    }


    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    @OneToOne(mappedBy = "fan")
    private Reward reward;
}