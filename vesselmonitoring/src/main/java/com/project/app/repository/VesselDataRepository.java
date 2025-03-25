package com.project.app.repository;

import com.project.app.model.Vessel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VesselDataRepository extends CassandraRepository<Vessel, Long> {

}
