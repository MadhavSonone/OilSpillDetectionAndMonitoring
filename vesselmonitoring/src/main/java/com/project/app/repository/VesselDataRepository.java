package com.project.app.repository;

import com.project.app.model.Vessel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VesselDataRepository extends CassandraRepository<Vessel, String> {

    // Find vessel by MMSI (String)
    Vessel findByMmsi(String mmsi);

    // Find multiple vessels by their MMSIs (String)
    List<Vessel> findByMmsiIn(List<String> mmsiList);
}
