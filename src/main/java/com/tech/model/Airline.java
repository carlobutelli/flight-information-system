package com.tech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "airline")
@ApiModel(value = "Airline", description = "Relation to represent Airline object")
public class Airline extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    @Pattern(regexp = "[0-9]{3}+")
    @ApiModelProperty(example = "055")
    private String icaoCode;

    @NotNull
    @Column(unique = true)
    @ApiModelProperty(example = "Alitalia")
    private String name;

    @NotNull
    @Pattern(regexp = "[A-Z0-9]{2}+")
    @Column(unique = true)
    @ApiModelProperty(example = "AZ")
    private String carrier;

    @Range(min = 0, max = 1)
    @Column(precision=1, scale=2)
    @ApiModelProperty(example = "0.0")
    private double delayedProbability = 0.0;

    @Range(min = 0, max = 1)
    @Column(precision=1, scale=2)
    @ApiModelProperty(example = "0.0")
    private double cancelledProbability = 0.0;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "airline2airport",
            joinColumns = @JoinColumn(name = "airlineId"),
            inverseJoinColumns = @JoinColumn(name = "airportId")
    )
    private List<Airport> airports = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Flight> flights;

    public Airline() {
    }

    public Airline(String icaoCode, String name, String carrier) {
        this.icaoCode = icaoCode;
        this.name = name;
        this.carrier = carrier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public void setId(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public double getDelayedProbability() {
        return delayedProbability;
    }

//    @JsonIgnore
    public void setDelayedProbability(double delayedProbability) {
        this.delayedProbability = delayedProbability;
    }

    public double getCancelledProbability() {
        return cancelledProbability;
    }

//    @JsonIgnore
    public void setCancelledProbability(double cancelledProbability) {
        this.cancelledProbability = cancelledProbability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.tech.model.Airline airline = (com.tech.model.Airline) o;
        return id == airline.id &&
                icaoCode.equals(airline.icaoCode) &&
                delayedProbability == airline.delayedProbability &&
                cancelledProbability == airline.cancelledProbability &&
                name.equals(airline.name) &&
                carrier.equals(airline.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, icaoCode, name, carrier, delayedProbability, cancelledProbability);
    }
}
