//package com.tech.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//import org.hibernate.validator.constraints.Range;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Objects;
//
//@Embeddable
//public class FlightId implements Serializable {
//
//    @Range(min=1, max=9999)
//    @Column(name = "flight_number")
//    private int flightNumber;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "airline_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private Airline airline;
//
//    public FlightId() {}
//
//    public FlightId(int flightNumber, Airline airline) {
//        this.flightNumber = flightNumber;
//        this.airline = airline;
//    }
//
//    public int getFlightNumber() {
//        return flightNumber;
//    }
//
//    public void setFlightNumber(int flightNumber) {
//        this.flightNumber = flightNumber;
//    }
//
//    public Airline getAirline() {
//        return airline;
//    }
//
//    public void setAirline(Airline airline) {
//        this.airline = airline;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        FlightId flightId = (FlightId) o;
//        return flightNumber == flightId.flightNumber &&
//                airline.equals(flightId.airline);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(flightNumber, airline);
//    }
//}
