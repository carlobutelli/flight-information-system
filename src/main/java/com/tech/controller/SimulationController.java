package com.tech.controller;

import com.tech.api.BaseResponse;
import com.tech.api.ErrorResponse;
import com.tech.repository.AirlineRepository;
import com.tech.repository.AirportRepository;
import com.tech.repository.FlightRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

@RestController
@Api(value = "Manages simulation")
public class SimulationController {

    private final Logger log =  LoggerFactory.getLogger(AirlineController.class);
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    public SimulationController(AirlineRepository airlineRepository,
                                AirportRepository airportRepository,
                                FlightRepository flightRepository) {
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
    }

    @PostMapping("/simulate")
    @ApiOperation(value = "Route to create simulation data")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity<?> createSimulation(@RequestParam(value = "currentTime", required = false) String currentTime) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to create simulations"
        );
        try {
            if(currentTime == null || !currentTime.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")){
                throw new IllegalArgumentException("The time you entered was not valid");
            }

            logInfoWithTransactionId(
                    transactionId,
                    String.format("requested simulation at %s", currentTime)
            );
            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    String.format("Parameter: %s", currentTime),
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error(String.format("[SIMULATION] %s: time format not valid %s", transactionId,
                    e.getLocalizedMessage()));
            return generateErrorResponse(400, "time format not valid or null", transactionId);
        } catch (Exception e) {
            log.error(String.format("[SIMULATION] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[SIMULATION] %s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private ResponseEntity generateErrorResponse(int statusCode, String message, String transactionId) {
        BaseResponse meta = new BaseResponse(transactionId, "ERROR", message, statusCode);
        ErrorResponse response = new ErrorResponse(meta);
        return ResponseEntity.status(statusCode).body(response);
    }

    private LocalDateTime generateLocalDateTime() {
        final Random random = new Random();
        return LocalDateTime
                .of(LocalDate.now(), LocalTime.of(random.nextInt(24), 5 * (Math.round(random.nextInt(60) / 5)), 0, 0));

    }

}
