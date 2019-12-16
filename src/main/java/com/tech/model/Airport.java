package com.tech.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name="airport")
public class Airport extends AuditModel {
    @Id
    @Pattern(regexp = "[A-Z]{3}+")
    private String iataCode;

    @NotNull
    @Size(max = 50)
    @Column(unique = true)
    private String name;

    @NotNull
    @Size(max = 45)
    @Column
    private String city;

    @NotNull
    @Size(max = 45)
    @Column
    private String country;

    public Airport() {}

    public Airport(@Pattern(regexp = "[A-Z]{3}+") String iataCode, String name, String city, String country) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return iataCode.equals(airport.iataCode) &&
                name.equals(airport.name) &&
                city.equals(airport.city) &&
                country.equals(airport.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iataCode, name, city, country);
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String id) {
        this.iataCode = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
