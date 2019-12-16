package com.tech.controller;

import com.tech.model.Airport;
import com.tech.repository.AirportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class AirportController {
    private final Logger log =  LoggerFactory.getLogger(AirportController.class);
    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @PostMapping("/airport")
    public Airport createAirport(@Valid @RequestBody Airport airport) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new airport"
        );
        return airportRepository.save(airport);
    }

    @GetMapping("/airport")
    public Iterable<Airport> getAirport() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add fetch list of airports"
        );
        return airportRepository.findAll();
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
