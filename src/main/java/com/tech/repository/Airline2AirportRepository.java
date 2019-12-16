package com.tech.repository;

import com.tech.model.Airline2Airport;
import com.tech.model.Airline2AirportId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface Airline2AirportRepository extends CrudRepository<Airline2Airport, Airline2AirportId> {

    List<Airline2Airport> findByAirlineIdAndAirportId(int airlineId, String airportId);
}
