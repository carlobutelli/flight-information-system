package com.tech.controller;

import com.tech.model.Flight;
import com.tech.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class FlightController {

    private final Logger log =  LoggerFactory.getLogger(FlightController.class);
    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @PostMapping("/flight")
    public Flight createFlight(@Valid @RequestBody Flight flight) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new flight"
        );
        return flightRepository.save(flight);
    }

    @GetMapping("/flight")
    public Iterable<Flight> getFlights() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch full list of flights"
        );
        return flightRepository.findAll();
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
