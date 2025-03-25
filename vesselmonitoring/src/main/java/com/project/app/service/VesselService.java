package com.project.app.service;

import com.project.app.model.Vessel;
import com.project.app.repository.VesselDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VesselService {

    @Autowired
    private VesselDataRepository latestAisDataRepository;

    public List<Vessel> getAllVessels() {
        return latestAisDataRepository.findAll();
    }
}
