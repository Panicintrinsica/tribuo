/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.types;

import jakarta.persistence.*;

@Entity
@Table(name = "reward")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_id", nullable = false)
    private String seatId; // Changed to String

    @Column(name = "fan_id", nullable = false, insertable = false, updatable = false)
    private Long fanId;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "fan_id", referencedColumnName = "fan_id")
    private Fan fan;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public Long getFanId() {
        return fanId;
    }

    public void setFanId(Long fanId) {
        this.fanId = fanId;
    }

    public void setFan(Fan fan) {
        this.fan = fan;
        if (fan != null) {
            this.fanId = fan.getId();
        }
    }


}