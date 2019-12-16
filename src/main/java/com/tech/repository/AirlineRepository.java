package com.tech.repository;

import com.sun.xml.bind.v2.model.core.ID;
import com.tech.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {

    void deleteAirlineByIcaoCode(int id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE airline SET pDelayed = ?3, pCancelled = ?2 WHERE id = ?1", nativeQuery = true)
    int updateProbabilities(int airlineId, int pCancelled, int pDelayed);


    Airline findOneById(int id);
}
