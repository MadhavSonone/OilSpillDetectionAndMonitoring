	package com.project.app.service;
	
	import com.project.app.model.Vessel;
	import com.project.app.model.UserVessel;
	import com.project.app.repository.VesselDataRepository;
	import com.project.app.repository.UserVesselRepository;
	
	import java.time.Instant;
	import java.util.Optional;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.scheduling.annotation.Scheduled;
	import org.springframework.stereotype.Service;
	
	@Service
	public class CheckAnomaly {
	
	    @Autowired
	    private VesselDataRepository vesselDataRepository;
	
	    @Autowired
	    private UserVesselRepository userVesselRepository;
	
	    @Autowired
	    private EmailService emailService;
	
	    @Scheduled(fixedRate = 30000)  // Run every 30 sec
	    public void checkVesselStatusAndSendAlerts() {
	        Iterable<Vessel> vessels = vesselDataRepository.findAll();
	
	        for (Vessel vessel : vessels) {
	            // Check if the vessel status is anomalous (e.g., not "UNDERWAY")
	            if ("ANOMALOUS_BEHAVIOR".equalsIgnoreCase(vessel.getStatus())) {
	                System.out.println("Anomaly detected in vessel " + vessel.getMmsi() + ". Status: " + vessel.getStatus());
	                sendAnomalyAlert(vessel);
	
	            } else {
	                System.out.println("Vessel " + vessel.getMmsi() + " is underway. No anomaly detected.");
	            }
	        }
	    }
	
	    private void sendAnomalyAlert(Vessel vessel) {
	        // Fetch the UserVessel from the repository using the MMSI number
	        Optional<UserVessel> userVesselOptional = userVesselRepository.findByMmsi(vessel.getMmsi());
	
	        // Check if the Optional contains a value
	        if (userVesselOptional.isPresent()) {
	            UserVessel userVessel = userVesselOptional.get(); // Get the actual UserVessel object
	            String mmsi = vessel.getMmsi();
	            double lat = vessel.getLatitude();
	            double lon = vessel.getLongitude();
	            Instant time = vessel.getTimestamp();
	            String status = vessel.getStatus();
	            String recipientEmail = userVessel.getEmail();  // Assuming UserVessel has a field for email
	            String subject = mmsi + " : Anomalous Behavior Observed!";
	            String message = "Vessel: " + mmsi + " has an anomalous status: " + status + ". Please investigate.\n" +
	                             "Latitude: " + lat + ", Longitude: " + lon + ", Timestamp: " + time;
	
	            // Send the email to the user associated with the vessel
	            emailService.sendEmail(recipientEmail, subject, message);
	        } else {
	            // Handle the case where no associated user was found
	            System.out.println("No associated user found for vessel " + vessel.getMmsi());
	        }
	    }
	}
