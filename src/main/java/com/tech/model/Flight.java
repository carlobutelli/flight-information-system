package com.tech.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

// Flights n to 1 Airlines
@Entity
@Table(name = "flight")
public class Flight extends AuditModel {

    public enum StatusEnum {SCHEDULED, OPERATING, CANCELLED}

    @Id
    @NotNull
    @Range(min=1, max=9999)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "flight_number", updatable = false)
    private int flightNumber;

    @Pattern(regexp = "[A-Z]{3}+")
    @Column(columnDefinition = "text", length = 3, nullable = false)
    private String source;

    @Pattern(regexp = "[A-Z]{3}+")
    @Column(columnDefinition = "text", length = 3, nullable = false)
    private String destination;

    @Column(nullable = false)
//    @Temporal(TemporalType.TIME)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private LocalDateTime estimatedTime;

    @Column(nullable = false)
    private LocalDateTime actualTime;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public Flight() { }

    public Flight(@Pattern(regexp = "[A-Z]{3}+") String source,
                  @Pattern(regexp = "[A-Z]{3}+") String destination,
                  LocalDateTime scheduledTime,
                  LocalDateTime estimatedTime,
                  LocalDateTime actualTime,
                  StatusEnum status) {
        this.source = source;
        this.destination = destination;
        this.scheduledTime = scheduledTime;
        this.estimatedTime = estimatedTime;
        this.actualTime = actualTime;
        this.status = status;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(LocalDateTime estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getActualTime() {
        return actualTime;
    }

    public void setActualTime(LocalDateTime actualTime) {
        this.actualTime = actualTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}