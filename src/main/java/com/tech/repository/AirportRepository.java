package com.tech.repository;

import com.tech.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
    Airport findOneByIataCode(String id);

    @Query(value = "select a.iataCode from #{#entityName} a")
    List<String> getAllIataCodes();
}
