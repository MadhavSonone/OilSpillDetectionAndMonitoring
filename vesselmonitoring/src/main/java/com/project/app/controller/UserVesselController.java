package com.project.app.controller;

import com.project.app.model.UserVessel;
import com.project.app.service.UserVesselService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserVesselController {

    private final UserVesselService userVesselService;

    public UserVesselController(UserVesselService userVesselService) {
        this.userVesselService = userVesselService;
    }

    @PostMapping("/addVessel")
    public String addVessel(@RequestParam String email, @RequestParam String mmsi, Model model) {
        userVesselService.addVesselToUser(email, mmsi);
        model.addAttribute("message", "Vessel added successfully!");
        return "confirmation";  // Return a view with the success message
    }

    @GetMapping("/getVessel/{email}")
    public String getVessel(@PathVariable String email, Model model) {
        // Fetch all vessels tracked by the user
        List<UserVessel> userVessels = userVesselService.getVesselsTrackedByUser(email);
        
        if (userVessels != null && !userVessels.isEmpty()) {
            model.addAttribute("userVessels", userVessels);  // Add the list of vessels to the model
            return "vesselDetails";  // Return a view showing the vessel details
        } else {
            model.addAttribute("message", "No vessels found for this user.");
            return "error";  // Return an error view if no vessels are found
        }
    }

    @DeleteMapping("/removeVessel/{email}")
    public String removeVessel(@PathVariable String email, Model model) {
        userVesselService.removeVessel(email);  // Assuming this removes the relationship for the user
        model.addAttribute("message", "Vessel removed successfully!");
        return "confirmation";  // Return a view with the success message
    }
}
