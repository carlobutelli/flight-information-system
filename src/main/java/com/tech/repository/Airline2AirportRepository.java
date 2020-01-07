package com.tech.repository;

import com.tech.model.Airline2Airport;
import com.tech.model.Airline2AirportId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Airline2AirportRepository extends CrudRepository<Airline2Airport, Airline2AirportId> {

    Airline2Airport findByAirlineIdAndAirportId(int airlineId, String airportId);

    List<Airline2Airport> findByAirportId(String airportId);

    @Modifying
    @Query("update Airline2Airport a2a set a2a.generated = false")
    @Transactional
    void updateGeneratedToFalse();

}
