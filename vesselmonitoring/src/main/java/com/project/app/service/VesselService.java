package com.project.app.service;

import com.project.app.model.Vessel;
import com.project.app.repository.VesselDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VesselService {

    @Autowired
    private VesselDataRepository vesselRepository;


    // Fetch all vessels (Admin only)
    public List<Vessel> getAllVessels() {
        return vesselRepository.findAll();  // Use findAll to fetch all vessel records
    }
    
    public Vessel updateVesselStatus(String mmsi, String status) {
        Vessel vessel = vesselRepository.findByMmsi(mmsi);

        if (vessel != null) {
            vessel.setStatus(status);  // Set the new status
            return vesselRepository.save(vessel); // Save updated vessel
        } else {
            throw new RuntimeException("Vessel not found for MMSI: " + mmsi);
        }
    }
}
