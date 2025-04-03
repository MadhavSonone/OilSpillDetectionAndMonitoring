package com.project.app.repository;
import com.project.app.model.UserVessel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.List;
import java.util.Optional;

public interface UserVesselRepository extends CassandraRepository<UserVessel, String> {

    // Fetch all vessels tracked by a user by their email (updated to return a List)
    List<UserVessel> findByEmail(String email);  // Returns a list of UserVessel objects associated with the email

    // Find a vessel by MMSI number
    Optional<UserVessel> findByMmsi(String mmsi); // Find vessel by MMSI (as before)
}
