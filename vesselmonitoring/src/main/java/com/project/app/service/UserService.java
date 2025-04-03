package com.project.app.service;

import com.project.app.model.User;
import com.project.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null; 
    }

    // ✅ Create Admin User if not exists
    public void createAdminUser() {
        if (userRepository.findByEmail("admin@example.com") == null) { 
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin@123")); 
            admin.setUserType("ADMIN"); 
            userRepository.save(admin);
            System.out.println("✅ Admin user created successfully.");
        } else {
            System.out.println("⚠️ Admin user already exists.");
        }
    }
}
