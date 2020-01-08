package com.tech.repository;

import com.tech.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    List<Flight> findAllByOrderByScheduledTimeAsc();

    List<Flight> findAllBySourceAndDestination(String source, String destination);

    List<Flight> findAllBySource(String source);

    List<Flight> findAllByDestination(String destination);

    @Query("select f from Flight f where f.fk_airline=:airlineId")
    List<Flight> findAllFlightsByAirlineId(int airlineId);

    String queryArrivals = "SELECT f " +
               "FROM Flight f " +
               "WHERE DATE(f.scheduledTime)=current_date " +
               "AND f.destination=:airport";
    @Query(queryArrivals)
    List<Flight> findArrivalsByAirportScheduledForToday(String airport);

    String queryArrivalsToday = "SELECT f " +
            "FROM Flight f " +
            "WHERE DATE(f.scheduledTime)=current_date " +
            "AND f.destination=:airport " +
            "ORDER BY f.scheduledTime ASC";
    @Query(queryArrivalsToday)
    List<Flight> findTodayArrivalsByAirportOrderByScheduledTimeAsc(String airport);

    String queryDeparturesToday = "SELECT f " +
            "FROM Flight f " +
            "WHERE DATE(f.scheduledTime)=current_date " +
            "AND f.source=:airport " +
            "ORDER BY f.scheduledTime ASC";
    @Query(queryDeparturesToday)
    List<Flight> findTodayDeparturesByAirportOrderByScheduledTimeAsc(String airport);

    String queryDepartures = "SELECT f " +
            "FROM Flight f " +
            "WHERE DATE(f.scheduledTime)=current_date " +
            "AND f.source=:airport ";
    @Query(queryDepartures)
    List<Flight> findDeparturesByAirportScheduledForToday(String airport);

    String queryFlights = "SELECT f " +
            "FROM Flight f " +
            "WHERE DATE(f.scheduledTime)=current_date " +
            "AND (f.source=:airport OR f.destination=:airport)";
    @Query(queryFlights)
    List<Flight> findFlightsByAirportScheduledForToday(String airport);

//    String updateArrivals =
//            "UPDATE Flight SET actualTime = CASE " +
//            "WHEN (scheduledTime < current_timestamp AND source=:airport) THEN scheduledTime " +
//            "WHEN (scheduledTime < current_timestamp AND destination=:airport) THEN scheduledTime " +
//            "WHEN (scheduledTime >= current_timestamp) THEN NULL " +
//            "END, status = CASE " +
//            "WHEN (scheduledTime < current_timestamp AND source=:airport) THEN 'DEPARTED' " +
//            "WHEN (scheduledTime < current_timestamp AND destination=:airport) THEN 'LANDED' " +
//            "WHEN (scheduledTime >= current_timestamp) THEN 'SCHEDULED' " +
//            "END FROM Flight f JOIN Airline a on f.fk_airline = a.id " +
//            "WHERE DATE(f.scheduledTime)=current_date " +
//            "AND (f.source=:airport OR f.destination=:airport) " +
//            "AND a.delayedProbability=0 " +
//            "AND a.cancelledProbability=0";
//    @Query(updateArrivals)
//    List<Flight> updateFlightsByAirportScheduledForToday(String airport);

}
