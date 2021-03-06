package com.tech.controller;

import com.tech.api.responses.ListDataResponse;
import com.tech.api.responses.BaseResponse;
import com.tech.api.responses.ErrorResponse;
import com.tech.api.payloads.ProbabilityPayload;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airline;
import com.tech.model.Airline2Airport;
import com.tech.repository.Airline2AirportRepository;
import com.tech.repository.AirlineRepository;
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
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/airlines")
@Api(value = "Manages Airlines objects")
public class AirlineController {

    private final Logger log =  LoggerFactory.getLogger(AirlineController.class);
    private final AirlineRepository airlineRepository;
    private final Airline2AirportRepository airline2AirportRepository;

    public AirlineController(AirlineRepository airlineRepository, Airline2AirportRepository airline2AirportRepository) {
        this.airlineRepository = airlineRepository;
        this.airline2AirportRepository = airline2AirportRepository;
    }

    @PostMapping
    @ApiOperation(value = "Route to add a new airline")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity createAirline(@Valid @RequestBody Airline airline) {

        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new airline"
        );
        try {
            if((airline.getCancelledProbability() + airline.getDelayedProbability()) > 1.0) {
                throw new IOException();
            }
            Airline newAirline = airlineRepository.save(airline);
            logInfoWithTransactionId(
                    transactionId,
                    String.format("airline %s successfully created", newAirline.getId())
            );
            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    String.format("airline with id %s successfully created", newAirline.getId()),
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error(String.format("[airline] %s sum of given probabilities exceeded 1.0", transactionId));
            return generateErrorResponse(400, "sum of probabilities exceeded 1.0", transactionId);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @GetMapping
    @ApiOperation(value = "Route to fetch the list of airlines")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity getAirlines() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch list of airlines"
        );
        try {
            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "airlines successfully retrieved",
                    200);
            List<?> airlines = airlineRepository.findAll();
            ListDataResponse response = new ListDataResponse(meta, airlines);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PatchMapping("/{airlineId}")
    @ApiOperation(value = "Route to update probabilities of given airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity updateAirlineProbabilities(@PathVariable int airlineId,
                                                        @Valid @RequestBody ProbabilityPayload probabilities) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                String.format("Got request to update probabilities of airline %s", airlineId)
        );
        try {
            if((probabilities.getCancelledProbability() + probabilities.getDelayedProbability()) > 1.0) {
                throw new IOException();
            }
            Airline airline = airlineRepository.findOneById(airlineId);
            airline.setDelayedProbability(probabilities.getDelayedProbability());
            airline.setCancelledProbability(probabilities.getCancelledProbability());
            airlineRepository.save(airline);
            BaseResponse meta = new BaseResponse(
                    "UPDATED",
                    transactionId,
                    String.format("airline %s successfully updated", airline.getId()),
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (IOException e) {
            log.error(String.format("[airline] %s sum of given probabilities exceeded 1.0", transactionId));
            return generateErrorResponse(400, "sum of probabilities exceeded 1.0", transactionId);
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[airline] %s airline %s not found: %s", transactionId, airlineId, e));
            return generateErrorResponse(404, "resource not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PutMapping("/{airlineId}")
    @ApiOperation(value = "Route to update airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity updateAirline(@PathVariable int airlineId, @Valid @RequestBody Airline airlineRequest) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to update given airline"
        );
        try {
            if((airlineRequest.getDelayedProbability() + airlineRequest.getCancelledProbability()) > 1.0) {
                throw new IOException();
            }
            return airlineRepository.findById(airlineId).map(airline -> {
                airline.setIcaoCode(airlineRequest.getIcaoCode());
                airline.setName(airlineRequest.getName());
                airline.setCarrier(airlineRequest.getCarrier());
                airline.setDelayedProbability(airlineRequest.getDelayedProbability());
                airline.setCancelledProbability(airlineRequest.getCancelledProbability());
                airlineRepository.save(airline);
                BaseResponse meta = new BaseResponse(
                        "UPDATED",
                        transactionId,
                        String.format("airline %s successfully updated", airline.getId()),
                        200);
                return new ResponseEntity<>(meta, HttpStatus.OK);
            }).orElseThrow(() -> new ResourceNotFoundException("Airline " + airlineId + " not found"));
        } catch (IOException e) {
            log.error(String.format("[airline] %s sum of given probabilities exceeded 1.0", transactionId));
            return generateErrorResponse(400, "sum of probabilities exceeded 1.0", transactionId);
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[ERROR] %s airline %s not found: %s", transactionId, airlineId, e));
            return generateErrorResponse(404, "resource not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @DeleteMapping("/{airlineId}")
    @ApiOperation(value = "Route to delete given airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity deleteAirline(@PathVariable int airlineId) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                String.format("Got request to delete airline %s", airlineId)
        );
        return airlineRepository.findById(airlineId).map(airline -> {
            airlineRepository.delete(airline);
            BaseResponse meta = new BaseResponse(
                    "DELETED",
                    transactionId,
                    String.format("airline %s successfully deleted", airline.getId()),
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException("Airline " + airlineId + " not found"));
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[AIRLINE] %s: %s", transactionId, message));
    }

    @PostMapping("/{airlineId}/arrivals-departures")
    @ApiOperation(value = "Route to add arrival/departures to a given airline")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "successful", response = BaseResponse.class)})
    public ResponseEntity addArrivalsDepartures(@PathVariable int airlineId,
                                                   @RequestParam(value = "airportId") String airportId,
                                                   @RequestParam(value = "numberOfArrivals") int numberOfArrivals,
                                                   @RequestParam(value = "numberOfDepartures") int numberOfDepartures) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add arrival/departures"
        );
        try {
            Airline2Airport a2a = airline2AirportRepository.save(
                    new Airline2Airport(airlineId,airportId, numberOfArrivals, numberOfDepartures)
            );

            logInfoWithTransactionId(
                    transactionId,
                    String.format("Arrivals and departures successfully set for airline %s", a2a.getAirlineId())
            );
            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    String.format("Arrivals and departures successfully set for airline %s", a2a.getAirlineId()),
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
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
