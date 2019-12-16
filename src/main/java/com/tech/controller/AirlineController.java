package com.tech.controller;

import com.tech.api.ProbabilityPayload;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airline;
import com.tech.repository.AirlineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class AirlineController {

    private final Logger log =  LoggerFactory.getLogger(AirlineController.class);
    private final AirlineRepository airlineRepository;

    public AirlineController(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @PostMapping("/airline")
    public Airline createAirline(@Valid @RequestBody Airline airline) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new airline"
        );
        return airlineRepository.save(airline);
    }

    @GetMapping("/airline")
    public Iterable<Airline> getAirlines() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch list of airlines"
        );
        return airlineRepository.findAll();
    }

    @PatchMapping("/airline/{airlineId}")
    public ResponseEntity<?> updateAirlineProbabilities(@PathVariable int airlineId, @Valid @RequestBody ProbabilityPayload probabilities) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                String.format("Got request to update probabilities of airline %s", airlineId)
        );
        try {
            Airline airline = airlineRepository.findOneById(airlineId);
            airline.setpDelayed(probabilities.getpDelayed());
            airline.setpCancelled(probabilities.getpCancelled());
            airlineRepository.save(airline);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[ERROR] %s airline %s not found: %s", transactionId, airlineId, e));
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/airline/{airlineId}")
    public Airline updateAirline(@PathVariable int airlineId, @Valid @RequestBody Airline airlineRequest) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to update probabilities of given airline"
        );
        return airlineRepository.findById(airlineId).map(airline -> {
            airline.setIcaoCode(airlineRequest.getIcaoCode());
            airline.setName(airlineRequest.getName());
            airline.setCarrier(airlineRequest.getCarrier());
            airline.setCountry(airlineRequest.getCountry());
            airline.setpDelayed(airlineRequest.getpDelayed());
            airline.setpCancelled(airlineRequest.getpCancelled());
            return airlineRepository.save(airline);
        }).orElseThrow(() -> new ResourceNotFoundException("Airline " + airlineId + " not found"));
    }

    @DeleteMapping("/airline/{airlineId}")
    public ResponseEntity<?> deleteAirline(@PathVariable int airlineId) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                String.format("Got request to delete airline %s", airlineId)
        );
        return airlineRepository.findById(airlineId).map(airline -> {
            airlineRepository.delete(airline);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Airlinem" + airlineId + " not found"));
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

}
