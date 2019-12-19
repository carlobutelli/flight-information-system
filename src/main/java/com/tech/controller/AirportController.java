package com.tech.controller;

import com.tech.api.responses.BaseResponse;
import com.tech.api.responses.ErrorResponse;
import com.tech.api.responses.ListDataResponse;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airport;
import com.tech.repository.AirportRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/airports")
@Api(value = "Manages Airports objects")
public class AirportController {
    private final Logger log =  LoggerFactory.getLogger(AirportController.class);
    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @PostMapping
    @ApiOperation(value = "Route to add a new airport")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity createAirport(@Valid @RequestBody Airport airport) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new airport"
        );
        try {
            Airport newAirport = airportRepository.save(airport);
            logInfoWithTransactionId(
                    transactionId,
                    String.format("airport %s successfully created", newAirport.getIataCode())
            );
            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    String.format("airport with id %s successfully created", newAirport.getIataCode()),
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.format("[airport] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }

    }

    @GetMapping
    @ApiOperation(value = "Route to fetch the list of airports")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity getAirport() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add fetch list of airports"
        );
        try {
            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "airports successfully retrieved",
                    200);
            List<Airport> airports = airportRepository.findAll();
            ListDataResponse response = new ListDataResponse(meta, airports);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[AIRPORT] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PutMapping("/{airportId}")
    @ApiOperation(value = "Route to update airport")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity updateAirport(@PathVariable String airportId, @Valid @RequestBody Airport airportRequest) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to update a given airport"
        );
        try {
            return airportRepository.findById(airportId).map(airport -> {
                airport.setName(airportRequest.getName());
                airport.setCity(airportRequest.getCity());
                airport.setCountry(airportRequest.getCountry());
                airportRepository.save(airport);
                BaseResponse meta = new BaseResponse(
                        "UPDATED",
                        transactionId,
                        String.format("airport %s successfully updated", airport.getIataCode()),
                        200);
                return new ResponseEntity<>(meta, HttpStatus.OK);
            }).orElseThrow(() -> new ResourceNotFoundException("Airport " + airportId + " not found"));
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[AIRPORT] %s airport %s not found: %s", transactionId, airportId, e));
            return generateErrorResponse(404, "resource not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[AIRPORT] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[AIRPORT] %s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private ResponseEntity generateErrorResponse(int statusCode, String message, String transactionId) {
        BaseResponse meta = new BaseResponse(transactionId, "ERROR", message, statusCode);
        ErrorResponse response = new ErrorResponse(meta);
        return ResponseEntity.status(statusCode).body(response);
    }
}
