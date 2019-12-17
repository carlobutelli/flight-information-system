package com.tech.repository;

import com.tech.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {

    void deleteAirlineByIcaoCode(int id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE airline SET delayedProbability = ?3, cancelledProbability = ?2 WHERE id = ?1", nativeQuery = true)
    int updateProbabilities(int airlineId, int cancelledProbability, int delayedProbability);


    Airline findOneById(int id);
}
