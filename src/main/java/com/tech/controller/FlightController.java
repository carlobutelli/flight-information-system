package com.tech.controller;

import com.tech.api.responses.BaseResponse;
import com.tech.api.responses.ErrorResponse;
import com.tech.api.responses.ListDataResponse;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Flight;
import com.tech.repository.FlightRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flights")
@Api(value = "Manages flights objects")
public class FlightController {

    private final Logger log = LoggerFactory.getLogger(FlightController.class);
    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @PostMapping
    @ApiOperation(value = "Route to add a new flight")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity createFlight(@Valid @RequestBody Flight flight) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new flight"
        );
        try {
            flight.setEstimatedTime(LocalDateTime.now());
            flight.setActualTime(null);
            flight.setStatus(Flight.StatusEnum.SCHEDULED);
            Flight newFlight = flightRepository.save(flight);
            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    String.format("flight with id %s successfully created", newFlight.getFlightNumber()),
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.format("[flight] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @GetMapping
    @ApiOperation(value = "Route to fetch the list of flights")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity getFlights() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch full list of flights"
        );
        try {
            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "flights successfully retrieved",
                    200);
            List<Flight> flights = flightRepository.findAllByOrderByScheduledTimeAsc();
            ListDataResponse response = new ListDataResponse(meta, flights);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[flights] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PutMapping("/{flightNumber}")
    @ApiOperation(value = "Route to update flight")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity updateFlight(@PathVariable int flightNumber, @Valid @RequestBody Flight flightRequest) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to update given flight"
        );
        try {
            return flightRepository.findById(flightNumber).map(flight -> {
                flight.setDestination(flightRequest.getDestination());
                flight.setFk_airline(flightRequest.getFk_airline());
                flight.setScheduledTime(flightRequest.getScheduledTime());
                flight.setSource(flightRequest.getSource());
                flightRepository.save(flight);
                BaseResponse meta = new BaseResponse(
                        "UPDATED",
                        transactionId,
                        String.format("flight %s successfully updated", flight.getFlightNumber()),
                        200);
                return new ResponseEntity<>(meta, HttpStatus.OK);
            }).orElseThrow(() -> new ResourceNotFoundException("Flight " + flightNumber + " not found"));
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @DeleteMapping("/{flightNumber}")
    @ApiOperation(value = "Route to delete given flight")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity deleteFlight(@PathVariable int flightNumber) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                String.format("got request to delete flight %s", flightNumber)
        );
        return flightRepository.findById(flightNumber).map(flight -> {
            flightRepository.delete(flight);
            BaseResponse meta = new BaseResponse(
                    "DELETED",
                    transactionId,
                    String.format("flight %s successfully deleted", flightNumber),
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException("Flight " + flightNumber + " not found"));
    }

    @GetMapping("/airport/{airportId}/arrivals")
    @ApiOperation(value = "Route to fetch the list of arrival flights for airport")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity getArrivals(@PathVariable String airportId) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "got new request to fetch list of arrival flights for airport"
        );
        try {
            List<?> flights = flightRepository.findAllByDestination(airportId);
            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "arrivals successfully retrieved",
                    200);
            ListDataResponse response = new ListDataResponse(meta, flights);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[flights] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @GetMapping("/airport/{airportId}/departures")
    @ApiOperation(value = "Route to fetch the list of departures flights for airport")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity getDepartures(@PathVariable String airportId) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "got new request to fetch list of departures flights for airport"
        );
        try {
            List<?> flights = flightRepository.findAllBySource(airportId);
            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "departures successfully retrieved",
                    200);
            ListDataResponse response = new ListDataResponse(meta, flights);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[flights] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[flight] %s: %s", transactionId, message));
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
