package com.project.app.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Table("latest_ais_data")
public class Vessel {
    @PrimaryKey
    private String mmsi;

    @Column("timestamp")
    private Instant timestamp;

    @Column("latitude")
    private Double latitude;

    @Column("longitude")
    private Double longitude;

    @Column("vessel_name")
    private String vesselName;

    @Column("status")
    private String status;

    // Getters and Setters
    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getStatus() {
        // Default to "NOT_DEFINED" if the status is null
        return status != null ? status : "UNDERWAY";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return vesselName;
    }

    public Instant getLastUpdate() {
        return timestamp;
    }

    public String getStatusName() {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("0", "NOT_DEFINED");
        statusMap.put("1", "UNDERWAY");
        statusMap.put("2", "MOORED");
        statusMap.put("3", "ANCHORED");
        statusMap.put("4", "RESTRICTED");
        statusMap.put("6", "ANOMALOUS_BEHAVIOR");

        // Use the status provided by the database or the default if not found
        return statusMap.getOrDefault(status, "UNDERWAY");
    }
}
