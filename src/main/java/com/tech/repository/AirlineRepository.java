package com.tech.repository;

import com.tech.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {

    Airline findOneById(int id);

    @Query("select a " +
           "from Airline2Airport a2a JOIN Airline a on a2a.airlineId = a.id " +
           "where a2a.airportId=:airportId")
    List<Airline> findAllAirlineByAirportId(String airportId);
}
