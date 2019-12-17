package com.tech.controller;

import com.tech.api.responses.*;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airline;
import com.tech.model.Airline2Airport;
import com.tech.model.Airport;
import com.tech.model.Flight;
import com.tech.repository.Airline2AirportRepository;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/simulation")
@Api(value = "Manages simulation")
public class SimulationController {

    private final Logger log = LoggerFactory.getLogger(AirlineController.class);
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final Airline2AirportRepository airline2AirportRepository;
    private final FlightRepository flightRepository;

    public SimulationController(AirlineRepository airlineRepository,
                                AirportRepository airportRepository,
                                Airline2AirportRepository airline2AirportRepository,
                                FlightRepository flightRepository) {
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.airline2AirportRepository = airline2AirportRepository;
        this.flightRepository = flightRepository;
    }

    @PostMapping("/generate")
    @ApiOperation(value = "Route to create simulation data")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity<?> createSimulationData(@Valid @RequestParam(value = "airportId") String airportId) {
        String transactionId = generateTransactionId();
        try {
            logInfoWithTransactionId(transactionId, "requested creation of simulation data");

            logInfoWithTransactionId(transactionId, "fetching airport");
            Airport airport = airportRepository.findOneByIataCode(airportId);

            List<Airline2Airport> airline2Airports =
                    airline2AirportRepository.findByAirportId(airportId);

            String[] airportIds =  {"FCO", "JFK", "AMS", "ORY", "CNN", "BNE"};

            Random r = new Random();
            int lower = 1;
            int upper = 17;

            for (Airline2Airport a2a : airline2Airports) {
                Airline airline = airlineRepository.findOneById(a2a.getAirlineId());
                if(airline == null) {
                    throw new ResourceNotFoundException(
                            String.format("[SIMULATION] %s: airline %s not found", transactionId, a2a.getAirlineId())
                    );
                }
                // Create arrivals flight for airline
                logInfoWithTransactionId(
                        transactionId,
                        String.format("starting generation of arrivals for airline %s", airline.getId()));
                for(int i=0; i < a2a.getNumOfArrivals(); i++) {
                    int duration = (int) (Math.random() * (upper - lower)) + lower;
                    LocalDateTime ldt = generateRandomLocalDateTime();
                    Flight flight = new Flight();
                    flight.setFlightNumber(r.nextInt(9999));
                    flight.setSource(airportIds[new Random().nextInt(airportIds.length)]);
                    flight.setDestination(airport.getIataCode());
                    flight.setFk_airline(airline.getId());
                    flight.setScheduledTime(ldt);
                    flight.setDuration((int) (Math.random() * (upper - lower)) + lower);
                    setActualTimeAndStatus(r, airline, duration, flight, ldt);
                    flightRepository.save(flight);
                }
                // Create departing flight for airline
                logInfoWithTransactionId(
                        transactionId,
                        String.format("starting generation of departures for airline %s", airline.getId()));
                for(int i=0; i <a2a.getNumOfDepartures(); i++) {
                    int duration = (int) (Math.random() * (upper - lower)) + lower;
                    LocalDateTime ldt = generateRandomLocalDateTime();
                    Flight flight = new Flight();
                    flight.setFlightNumber(r.nextInt(9999));
                    flight.setSource(airport.getIataCode());
                    flight.setDestination(airportId);
                    flight.setFk_airline(airline.getId());
                    flight.setScheduledTime(ldt);
                    flight.setDuration(duration);
                    setActualTimeAndStatus(r, airline, duration, flight, ldt);
                    flightRepository.save(flight);
                }
            }

            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    "Simulation data created successfully",
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.format("[SIMULATION] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private void setActualTimeAndStatus(Random r, Airline airline, int duration, Flight flight, LocalDateTime ldt) {
        if (airline.getDelayedProbability() == 100) {
            flight.setStatus(Flight.StatusEnum.DELAYED);
            flight.setEstimatedTime(flight.getScheduledTime().plusMinutes(16));
            flight.setActualTime(flight.getScheduledTime().plusMinutes(r.nextInt(180)));
            if (flight.getActualTime().isBefore(LocalDateTime.now())) {
                flight.setStatus(Flight.StatusEnum.OPERATING);
            } else flight.setStatus(Flight.StatusEnum.SCHEDULED);
        } else if (airline.getCancelledProbability() == 100) {
            flight.setStatus(Flight.StatusEnum.CANCELLED);
            flight.setEstimatedTime(null);
            flight.setActualTime(null);
        } else if (flight.getScheduledTime().isBefore(LocalDateTime.now()) &&
                LocalDateTime.now().isBefore(flight.getScheduledTime().plusHours(duration))) {
            flight.setStatus(Flight.StatusEnum.OPERATING);
            flight.setEstimatedTime(ldt);
            flight.setActualTime(ldt);
        } else {
            flight.setStatus(Flight.StatusEnum.SCHEDULED);
            flight.setEstimatedTime(ldt);
            flight.setActualTime(ldt);
        }
    }

    @PostMapping("/{airportId}")
    @ApiOperation(value = "Route to simulation system evolving by passing custom current time")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "successful", response = SimulationResponse.class)})
    public ResponseEntity<?> simulate(@PathVariable String airportId,
                                      @RequestParam(value = "currentTime", required = false) String currentTime) {
        String transactionId = generateTransactionId();
        try {
            if (currentTime == null) {
                currentTime = LocalDateTime.now().toString();
            } else if ( !currentTime.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")) {
                throw new IllegalArgumentException("The time you entered was not valid");
            }
            logInfoWithTransactionId(transactionId, String.format("requested simulation at %s", currentTime));
            logInfoWithTransactionId(
                    transactionId,
                    String.format("fetching arrival flights for destination %s", airportId)
            );
            List<Flight> arrivalFlights = flightRepository.findAllByDestination(airportId);
            List<ArrivalsResponse> arrivalsResponses = new ArrayList<>();
            for (Flight f: arrivalFlights) {
                Airline airline = airlineRepository.findOneById(f.getFk_airline());
                ArrivalsResponse ar = new ArrivalsResponse();
                ar.setFlight(airline.getCarrier() + f.getFlightNumber());
                ar.setSource(f.getSource());
                ar.setScheduledTime(f.getScheduledTime().getHour() + ":" + f.getScheduledTime().getMinute());
                ar.setEstimatedTime(f.getEstimatedTime().getHour() + ":" + f.getEstimatedTime().getMinute());
                ar.setActualTime(f.getActualTime().getHour() + ":" + f.getActualTime().getMinute());
                ar.setStatus(f.getStatus().toString());
                arrivalsResponses.add(ar);
            }

            List<DeparturesResponse> departuresResponses = new ArrayList<>();
            logInfoWithTransactionId(
                    transactionId,
                    String.format("fetching departures flights for source %s", airportId)
            );
            List<Flight> departureFlights = flightRepository.findAllBySource(airportId);
            for (Flight f: departureFlights) {
                Airline airline = airlineRepository.findOneById(f.getFk_airline());
                DeparturesResponse dr = new DeparturesResponse();
                dr.setFlight(airline.getCarrier() + f.getFlightNumber());
                dr.setDestination(f.getDestination());
                dr.setScheduledTime(f.getScheduledTime().getHour() + ":" + f.getScheduledTime().getMinute());
                dr.setEstimatedTime(f.getEstimatedTime().getHour() + ":" + f.getEstimatedTime().getMinute());
                dr.setActualTime(f.getActualTime().getHour() + ":" + f.getActualTime().getMinute());
                dr.setStatus(f.getStatus().toString());
                departuresResponses.add(dr);
            }

            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "Simulation executed successfully",
                    200);
            SimulationResponse simulationResponse = new SimulationResponse();
            simulationResponse.setMeta(meta);
            simulationResponse.setArrivals(arrivalsResponses);
            simulationResponse.setDepartures(departuresResponses);
            return new ResponseEntity<>(simulationResponse, HttpStatus.OK);
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

    private LocalDateTime generateRandomLocalDateTime() {
        final Random random = new Random();
        return LocalDateTime
                .of(LocalDate.now(), LocalTime.of(random.nextInt(24), 5 * (Math.round(random.nextInt(60) / 5)), 0, 0));
    }
}
