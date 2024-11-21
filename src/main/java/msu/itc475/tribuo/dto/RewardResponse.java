/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.dto;

public class RewardResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String preferredStand;
    private String seatId;

    public RewardResponse(String firstName, String lastName, String email, String phone, String preferredStand, String seatId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.preferredStand = preferredStand;
        this.seatId = seatId;
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

    public String getPreferredStand() {
        return preferredStand;
    }

    public void setPreferredStand(String preferredStand) {
        this.preferredStand = preferredStand;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
}