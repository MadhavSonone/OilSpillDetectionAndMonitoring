package com.project.app.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user") // Ensure this matches your Cassandra table name
public class User {
    @PrimaryKey
    private String email; // Email is the primary key

    private String fname;
    private String lname;
    private String password;
    private String userType = "user"; // Default user type

    // Constructors
    public User() {}

    public User(String fname, String lname, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.userType = "user"; // Ensure default value
    }

    // Getters and Setters
    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
