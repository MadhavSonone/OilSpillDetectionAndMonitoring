package com.project.app.controller;

import com.project.app.model.Vessel;
import com.project.app.service.VesselService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/vessels")
@CrossOrigin(origins = "*") // Allow frontend access
public class VesselController {

    @Autowired
    private VesselService vesselService;

    // Fetch all available vessels (Admin only)
    @GetMapping("/all")
    public List<Vessel> getAllVessels() {
        return vesselService.getAllVessels();
    }
}
