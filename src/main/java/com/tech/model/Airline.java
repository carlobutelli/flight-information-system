package com.tech.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "airline")
public class Airline extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    @Pattern(regexp = "[0-9]{3}+")
    private String icaoCode;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    @Pattern(regexp = "[A-Z0-9]{2}+")
    @Column(unique = true)
    private String carrier;

    @NotNull
    @Size(max = 100)
    @Column(columnDefinition = "text")
    private String country;

    @Column
    @NotNull
    @Range(min = 0, max = 100)
    private int pDelayed;

    @Range(min = 0, max = 100)
    @Column
    @NotNull
    private int pCancelled;

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
