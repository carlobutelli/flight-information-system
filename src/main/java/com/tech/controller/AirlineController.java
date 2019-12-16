package com.tech.controller;

import com.tech.api.ListDataResponse;
import com.tech.api.BaseResponse;
import com.tech.api.ErrorResponse;
import com.tech.api.payloads.ProbabilityPayload;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airline;
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
import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "Manages Airlines objects")
public class AirlineController {

    private final Logger log =  LoggerFactory.getLogger(AirlineController.class);
    private final AirlineRepository airlineRepository;

    public AirlineController(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @PostMapping("/airline")
    @ApiOperation(value = "Route to add a new airline")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity<?> createAirline(@Valid @RequestBody Airline airline) {

        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to add new airline"
        );
        try {
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
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @GetMapping("/airline")
    @ApiOperation(value = "Route to fetch the list of airlines")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = ListDataResponse.class)})
    public ResponseEntity<ListDataResponse> getAirlines() {
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
            List<Airline> airlines = airlineRepository.findAll();
            ListDataResponse response = new ListDataResponse(meta, airlines);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PatchMapping("/airline/{airlineId}")
    @ApiOperation(value = "Route to update probabilities of given airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity<?> updateAirlineProbabilities(@PathVariable int airlineId,
                                                        @Valid @RequestBody ProbabilityPayload probabilities) {
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
            BaseResponse meta = new BaseResponse(
                    "UPDATED",
                    transactionId,
                    String.format("airline %s successfully updated", airline.getId()),
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[airline] %s airline %s not found: %s", transactionId, airlineId, e));
            return generateErrorResponse(404, "resource not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @PutMapping("/airline/{airlineId}")
    @ApiOperation(value = "Route to update airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity<?> updateAirline(@PathVariable int airlineId, @Valid @RequestBody Airline airlineRequest) {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(
                transactionId,
                "Got request to update given airline"
        );
        try {
            return airlineRepository.findById(airlineId).map(airline -> {
                airline.setIcaoCode(airlineRequest.getIcaoCode());
                airline.setName(airlineRequest.getName());
                airline.setCarrier(airlineRequest.getCarrier());
                airline.setCountry(airlineRequest.getCountry());
                airline.setpDelayed(airlineRequest.getpDelayed());
                airline.setpCancelled(airlineRequest.getpCancelled());
                airlineRepository.save(airline);
                BaseResponse meta = new BaseResponse(
                        "UPDATED",
                        transactionId,
                        String.format("airline %s successfully updated", airline.getId()),
                        200);
                return new ResponseEntity<>(meta, HttpStatus.OK);
            }).orElseThrow(() -> new ResourceNotFoundException("Airline " + airlineId + " not found"));
        } catch (ResourceNotFoundException e) {
            log.error(String.format("[ERROR] %s airline %s not found: %s", transactionId, airlineId, e));
            return generateErrorResponse(404, "resource not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[airline] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @DeleteMapping("/airline/{airlineId}")
    @ApiOperation(value = "Route to delete given airline")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity<?> deleteAirline(@PathVariable int airlineId) {
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

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private ResponseEntity generateErrorResponse(int statusCode, String message, String transactionId) {
        BaseResponse meta = new BaseResponse(transactionId, "ERROR", message, statusCode);
        ErrorResponse response = new ErrorResponse(meta);
        return ResponseEntity.status(statusCode).body(response);
    }
}
