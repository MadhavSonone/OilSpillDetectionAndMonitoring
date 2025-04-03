package com.project.app.service;

import com.project.app.model.UserVessel;
import com.project.app.model.Vessel;
import com.project.app.repository.UserVesselRepository;
import com.project.app.repository.VesselDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserVesselService {

    private final UserVesselRepository userVesselRepository;
    private final VesselDataRepository vesselRepository;

    @Autowired
    public UserVesselService(UserVesselRepository userVesselRepository, VesselDataRepository vesselRepository) {
        this.userVesselRepository = userVesselRepository;
        this.vesselRepository = vesselRepository;
    }

    // Fetch vessel by MMSI number
    public Vessel getVesselByMmsi(String mmsi) {
        return vesselRepository.findByMmsi(mmsi);  // Assuming vesselRepository has a method to fetch vessel by MMSI
    }

    // Get list of vessels tracked by the user (updated to return List)
    public List<UserVessel> getVesselsTrackedByUser(String email) {
        return userVesselRepository.findByEmail(email);  // Fetches all UserVessel objects for the user
    }

    // Add vessel to the user's tracked vessels
    public void addVesselToUser(String email, String mmsi) {
        UserVessel userVessel = new UserVessel();
        userVessel.setEmail(email);
        userVessel.setMmsi(mmsi);
        userVesselRepository.save(userVessel);  // Save the user-vessel relationship
    }

    // Remove a vessel from the user's tracked vessels (Optional method)
    public void removeVessel(String email) {
        userVesselRepository.deleteById(email);  // Assuming you want to delete the user-vessel entry by email
    }
}
