package com.project.app.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

@Table("uservesseltable")
public class UserVessel {

    @PrimaryKey
    private String email;  // Email as the primary key

    @Column("mmsi")
    private String mmsi;  // MMSI of the vessel being tracked

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }
}
