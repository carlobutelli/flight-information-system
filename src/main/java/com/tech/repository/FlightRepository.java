package com.tech.repository;

import com.tech.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findAllBySourceAndDestination(String source, String destination);
    List<Flight> findAllBySource(String source);
    List<Flight> findAllByDestination(String destination);
}
