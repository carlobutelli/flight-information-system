package com.tech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "airline")
@ApiModel(value = "Airline", description = "Relation to represent Airline object")
public class Airline extends AuditModel {

    @Id
    @JsonIgnore
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

    @NotNull
    @Size(max = 100)
    @Column(columnDefinition = "text")
    @ApiModelProperty(example = "Italy")
    private String country;

    @Column
    @NotNull
    @Range(min = 0, max = 100)
    @ApiModelProperty(example = "5")
    private int pDelayed;

    @Range(min = 0, max = 100)
    @Column
    @NotNull
    @ApiModelProperty(example = "0")
    private int pCancelled;

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

    public Airline(String icaoCode, String name, String carrier, String country,
                   @Range(min = 0, max = 100) int pDelayed,
                   @Range(min = 0, max = 100) int pCancelled) {
        this.icaoCode = icaoCode;
        this.name = name;
        this.carrier = carrier;
        this.country = country;
        this.pDelayed = pDelayed;
        this.pCancelled = pCancelled;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getpDelayed() {
        return pDelayed;
    }

    public void setpDelayed(int pDelayed) {
        this.pDelayed = pDelayed;
    }

    public int getpCancelled() {
        return pCancelled;
    }

    public void setpCancelled(int pCancelled) {
        this.pCancelled = pCancelled;
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
                pDelayed == airline.pDelayed &&
                pCancelled == airline.pCancelled &&
                name.equals(airline.name) &&
                carrier.equals(airline.carrier) &&
                country.equals(airline.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, icaoCode, name, carrier, country, pDelayed, pCancelled);
    }
}
