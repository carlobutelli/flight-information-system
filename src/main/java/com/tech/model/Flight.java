package com.tech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight")
@ApiModel(value = "Flight", description = "Relation to represent Flight object")
public class Flight extends AuditModel {

    public enum StatusEnum {
        SCHEDULED,
        LANDED,
        DEPARTED,
        DELAYED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_number", updatable = false)
    private int flightNumber;

    @ManyToOne
    @JoinColumn(name = "source", insertable = false, updatable = false)
    private Airport sourceAirport;

    @NotNull
    @Length(max = 3)
    @Column(name = "source", columnDefinition = "text")
    @Pattern(regexp = "[A-Z]{3}+")
    @ApiModelProperty(example = "FCO")
    private String source;

    @ManyToOne
    @JoinColumn(name = "destination", insertable = false, updatable = false)
    private Airport destinationAirport;

    @NotNull
    @Length(max = 3)
    @Column(name = "destination", columnDefinition = "text")
    @Pattern(regexp = "[A-Z]{3}+")
    @ApiModelProperty(example = "JFK")
    private String destination;

    @Column
    @NotNull
    private LocalDateTime scheduledTime;

    @JsonIgnore
    @Column
    private LocalDateTime estimatedTime;

    @Column
    private LocalDateTime actualTime;

    @Column
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "fk_airline")
    private int fk_airline;

    @ManyToOne
    @JoinColumn(name = "fk_airline", insertable = false, updatable = false)
    private Airline airline;

    public int getFk_airline() {
        return fk_airline;
    }

    public void setFk_airline(int fk_airline) {
        this.fk_airline = fk_airline;
    }

    public Flight() {
    }

    public Flight(@Pattern(regexp = "[A-Z]{3}+") String source,
                  @Pattern(regexp = "[A-Z]{3}+") String destination,
                  LocalDateTime scheduledTime,
                  LocalDateTime estimatedTime,
                  LocalDateTime actualTime,
                  StatusEnum status,
                  int fk_airline) {
        this.source = source;
        this.destination = destination;
        this.scheduledTime = scheduledTime;
        this.estimatedTime = estimatedTime;
        this.actualTime = actualTime;
        this.status = status;
        this.fk_airline = fk_airline;
    }

    public int getFlightNumber() {
        return flightNumber;
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

    @JsonIgnore
    public void setEstimatedTime(LocalDateTime estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getActualTime() {
        return actualTime;
    }

    @JsonIgnore
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