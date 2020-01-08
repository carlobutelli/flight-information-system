package com.tech.controller;

import com.tech.api.responses.*;
import com.tech.exception.ResourceNotFoundException;
import com.tech.model.Airline;
import com.tech.model.Airline2Airport;
import com.tech.model.DelayedCancelledArrays;
import com.tech.model.Flight;
import com.tech.repository.Airline2AirportRepository;
import com.tech.repository.AirlineRepository;
import com.tech.repository.AirportRepository;
import com.tech.repository.FlightRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/simulation")
@Api(value = "Manages simulation")
public class SimulationController {

    private final Logger log = LoggerFactory.getLogger(AirlineController.class);
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final Airline2AirportRepository airline2AirportRepository;
    private final FlightRepository flightRepository;
    private final Random r = new Random();

    public SimulationController(AirportRepository airportRepository,
                                AirlineRepository airlineRepository,
                                Airline2AirportRepository airline2AirportRepository,
                                FlightRepository flightRepository) {
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.airline2AirportRepository = airline2AirportRepository;
        this.flightRepository = flightRepository;
    }

    @PostMapping("/flights/generate")
    @ApiOperation(value = "Route to populate flight table to be used in simulation")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 201, message = "created", response = BaseResponse.class)})
    public ResponseEntity createSimulationData(
            @Valid @RequestParam(value = "airportId") @ApiParam(value = "Iata code", example = "FCO") String airportId) {
        String transactionId = generateTransactionId();
        try {
            logInfoWithTransactionId(transactionId, "init flight generation");

            logInfoWithTransactionId(transactionId, "retrieving number of arrivals/departures per airline");
            List<Airline2Airport> airline2Airports = airline2AirportRepository.findByAirportId(airportId);
            if (airline2Airports == null || airline2Airports.isEmpty()) {
                throw new ResourceNotFoundException(String.format("airport %s not found", airportId));
            }

            // IataCodes to be used for random source/destination in arrivals/departures
            List<String> airportIds = airportRepository.getAllIataCodes();
            airportIds.remove(airportId);

            // for each combination airline - airport generate flights at first all set to SCHEDULED
            for (Airline2Airport a2a : airline2Airports) {
                if(a2a.isGenerated()) {
                    log.info(
                            String.format("number of arrivals/departures for airline %s already matched",
                                    a2a.getAirlineId()));
                    continue;
                }
                generateArrivalsAndDepartures(
                        transactionId,
                        airportId,
                        a2a.getAirlineId(),
                        airportIds,
                        a2a.getNumOfDepartures(),
                        a2a.getNumOfArrivals()
                );
                a2a.setGenerated(true);
                airline2AirportRepository.save(a2a);
            }

            BaseResponse meta = new BaseResponse(
                    "CREATED",
                    transactionId,
                    "Flights created successfully",
                    201);
            return new ResponseEntity<>(meta, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.format("[SIMULATION] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @DeleteMapping("/flights")
    @ApiOperation(value = "Route to clean flight table - WARNING! It will delete all the rows")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "resource not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "success", response = BaseResponse.class)})
    public ResponseEntity deleteFlight() {
        String transactionId = generateTransactionId();
        logInfoWithTransactionId(transactionId, "got request to clean data into flight table");
        try {
            flightRepository.deleteAll();
            airline2AirportRepository.updateGeneratedToFalse();
            BaseResponse meta = new BaseResponse(
                    "DELETED",
                    transactionId,
                    "flight table successfully cleaned",
                    200);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[SIMULATION] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private void generateArrivalsAndDepartures(String transactionId,
                                               String airportId,
                                               int airlineId,
                                               List<String> airportIds,
                                               int departuresCount,
                                               int arrivalsCount) {
        logInfoWithTransactionId(
                transactionId,
                String.format("generating arrival flights for airline %s", airlineId)
        );
        for (int i = 0; i < departuresCount; i++) {
            LocalDateTime ldt = generateRandomLocalDateTime();
            Flight flight = new Flight();
            flight.setFk_airline(airlineId);
            flight.setSource(airportIds.get(new Random().nextInt(airportIds.size())));
            flight.setDestination(airportId);
            flight.setScheduledTime(ldt);
            flight.setEstimatedTime(ldt);
            flight.setActualTime(null);
            flight.setStatus(Flight.StatusEnum.SCHEDULED);
            flightRepository.save(flight);
        }
        logInfoWithTransactionId(
                transactionId,
                String.format("generating departures flights for airline %s", airlineId));
        for (int i = 0; i < arrivalsCount; i++) {
            LocalDateTime ldt = generateRandomLocalDateTime();
            Flight flight = new Flight();
            flight.setFk_airline(airlineId);
            flight.setSource(airportId);
            flight.setDestination(airportIds.get(new Random().nextInt(airportIds.size())));
            flight.setScheduledTime(ldt);
            flight.setEstimatedTime(ldt);
            flight.setActualTime(null);
            flight.setStatus(Flight.StatusEnum.SCHEDULED);
            flightRepository.save(flight);
        }
    }


    @PostMapping("/{airportId}/simulate")
    @ApiOperation(value = "Route to simulation system evolving by passing custom current time")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "successful", response = SimulationResponse.class)})
    public ResponseEntity simulate(@PathVariable
                                   @ApiParam(value = "Iata code airport", example = "FCO") String airportId,
                                   @RequestParam(value = "currentTime", required = false)
                                   @ApiParam(value = "custom current time", example = "2019-12-19T11:50")
                                           String customTime) {
        String transactionId = generateTransactionId();
        try {
            LocalDateTime currentTime;

            if (customTime == null) {
                currentTime = LocalDateTime.now();
            } else {
                currentTime = LocalDateTime.parse(customTime,DateTimeFormatter.ISO_DATE_TIME);
            }
            logInfoWithTransactionId(transactionId, String.format("requested simulation at %s", currentTime));

            List<Integer> flightsToBeSetDelayed = getFlightsToBeSetDelayedOrCancelled(airportId).getDelayedFlights();
            log.info(String.format("==> Flights to be delayed: %s", flightsToBeSetDelayed));

            List<Integer> flightsToBeSetCancelled = getFlightsToBeSetDelayedOrCancelled(airportId).getCancelledFlights();
            log.info(String.format("==> Flights to be cancelled: %s", flightsToBeSetCancelled));

            List<Flight> arrivalFlights = flightRepository.findArrivalsByAirportScheduledForToday(airportId);

            for (Flight f: arrivalFlights) {
                if(flightsToBeSetDelayed.contains(f.getFlightNumber())) {
                    if(f.getScheduledTime().isBefore(currentTime)) {
                        f.setEstimatedTime(currentTime.plusMinutes(20));
                        f.setStatus(Flight.StatusEnum.DELAYED);
                        f.setActualTime(null);
                    } else {
                        f.setEstimatedTime(f.getScheduledTime().plusMinutes(20));
                        f.setStatus(Flight.StatusEnum.DELAYED);
                        f.setActualTime(null);
                    }
                } else if(flightsToBeSetCancelled.contains(f.getFlightNumber())) {
                    f.setActualTime(null);
                    f.setStatus(Flight.StatusEnum.CANCELLED);
                } else {
                    if(f.getEstimatedTime().isBefore(currentTime)) {
                        f.setActualTime(f.getEstimatedTime());
                        f.setStatus(Flight.StatusEnum.LANDED);
                    } else {
                        f.setActualTime(null);
                        f.setStatus(Flight.StatusEnum.SCHEDULED);
                    }
                }
               flightRepository.save(f);
            }

            List<ArrivalsResponse> arrivalsResponseList = getArrivalsResponses(airportId);

            List<Flight> departureFlights = flightRepository.findDeparturesByAirportScheduledForToday(airportId);
            for (Flight f: departureFlights) {
                if(flightsToBeSetDelayed.contains(f.getFlightNumber())) {
                    if(f.getScheduledTime().isBefore(currentTime)) {
                        f.setEstimatedTime(currentTime.plusMinutes(20));
                        f.setStatus(Flight.StatusEnum.DELAYED);
                        f.setActualTime(null);
                    } else {
                        f.setEstimatedTime(f.getScheduledTime().plusMinutes(20));
                        f.setStatus(Flight.StatusEnum.DELAYED);
                        f.setActualTime(null);
                    }
                } else if(flightsToBeSetCancelled.contains(f.getFlightNumber())) {
                    f.setActualTime(null);
                    f.setStatus(Flight.StatusEnum.CANCELLED);
                } else {
                    if(f.getEstimatedTime().isBefore(currentTime)) {
                        f.setActualTime(f.getEstimatedTime());
                        f.setStatus(Flight.StatusEnum.DEPARTED);
                    } else {
                        f.setActualTime(null);
                        f.setStatus(Flight.StatusEnum.SCHEDULED);
                    }
                }
                flightRepository.save(f);
            }

            List<DeparturesResponse> departuresResponseList = getDeparturesResponses(airportId);

            if (departureFlights.isEmpty() && arrivalFlights.isEmpty()) {
                log.error(String.format("[SIMULATION] %s: no data found", transactionId));
                return generateErrorResponse(404, "no data found", transactionId);
            }

            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "Simulation executed successfully",
                    200);

            SimulationResponse simulationResponse = new SimulationResponse();
            simulationResponse.setMeta(meta);
            simulationResponse.setArrivals(arrivalsResponseList);
            simulationResponse.setDepartures(departuresResponseList);
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

    @GetMapping("/{airportId}/flights")
    @ApiOperation(value = "Route to get simulated flights grouped by arrivals/departures")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "internal server error", response = ErrorResponse.class),
            @ApiResponse(code = 200, message = "successful", response = SimulationResponse.class)})
    public ResponseEntity simulate(@PathVariable
                                   @ApiParam(value = "Iata code airport", example = "FCO") String airportId) {
        String transactionId = generateTransactionId();
        try {
            List<ArrivalsResponse> arrivalsResponseList = getArrivalsResponses(airportId);
            List<DeparturesResponse> departuresResponseList = getDeparturesResponses(airportId);

            BaseResponse meta = new BaseResponse(
                    "OK",
                    transactionId,
                    "Simulated flights returned successfully",
                    200);

            SimulationResponse simulationResponse = new SimulationResponse();
            simulationResponse.setMeta(meta);
            simulationResponse.setArrivals(arrivalsResponseList);
            simulationResponse.setDepartures(departuresResponseList);
            return new ResponseEntity<>(simulationResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[SIMULATION] %s: error %s", transactionId, e.getLocalizedMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private List<DeparturesResponse> getDeparturesResponses(String airportId) {
        List<DeparturesResponse> departuresResponseList = new ArrayList<>();
        List<Flight> departures = flightRepository.findTodayDeparturesByAirportOrderByScheduledTimeAsc(airportId);
        for(Flight flight : departures) {
            DeparturesResponse dr = new DeparturesResponse(
                    String.valueOf(flight.getFlightNumber()),
                    flight.getDestination(),
                    generateTimeForResponse(
                            flight.getScheduledTime().getHour(),
                            flight.getScheduledTime().getMinute()
                    ),
                    generateTimeForResponse(
                            flight.getEstimatedTime().getHour(),
                            flight.getEstimatedTime().getMinute()
                    ),
                    flight.getStatus()

            );
            if(flight.getActualTime() != null)
                dr.setActualTime(generateTimeForResponse(
                        flight.getActualTime().getHour(),
                        flight.getActualTime().getMinute())
                );
            departuresResponseList.add(dr);
        }
        return departuresResponseList;
    }

    private List<ArrivalsResponse> getArrivalsResponses(String airportId) {
        List<ArrivalsResponse> arrivalsResponseList = new ArrayList<>();
        List<Flight> arrivals = flightRepository.findTodayArrivalsByAirportOrderByScheduledTimeAsc(airportId);
        for(Flight flight : arrivals) {
            ArrivalsResponse ar = new ArrivalsResponse(
                    String.valueOf(flight.getFlightNumber()),
                    flight.getSource(),
                    generateTimeForResponse(
                            flight.getScheduledTime().getHour(),
                            flight.getScheduledTime().getMinute()
                    ),
                    generateTimeForResponse(
                            flight.getEstimatedTime().getHour(),
                            flight.getEstimatedTime().getMinute()
                    ),
                    flight.getStatus()

            );
            if(flight.getActualTime() != null)
                ar.setActualTime(generateTimeForResponse(
                        flight.getActualTime().getHour(),
                        flight.getActualTime().getMinute())
                );
            arrivalsResponseList.add(ar);
        }
        return arrivalsResponseList;
    }

    private DelayedCancelledArrays getFlightsToBeSetDelayedOrCancelled(
            @ApiParam(value = "Iata code airport", example = "FCO") @PathVariable String airportId) {

        List<Integer> flightsToBeSetDelayed = new ArrayList<>();
        List<Integer> flightsToBeSetCancelled = new ArrayList<>();
        DelayedCancelledArrays delayedCancelledArrays = new DelayedCancelledArrays();

        List<Airline> airlines = airlineRepository.findAllAirlineByAirportId(airportId);
        log.info("Airlines: " + airlines.size());

        int numFlightsToBeDelayed = 0, numFlightsToBeSetCancelled = 0;

        for (Airline airline : airlines) {
            List<Flight> flights = flightRepository.findAllFlightsByAirlineId(airline.getId());
            log.info(String.format("Airline has %s Flights: ", flights.size()));

            ArrayList<Integer> baseNumberFlights = new ArrayList<>();

            if(airline.getDelayedProbability() != 0.0)
                numFlightsToBeDelayed = (int) Math.round(airline.getDelayedProbability() * flights.size());

            log.info("Flights to be delayed: " + numFlightsToBeDelayed);

            if(airline.getCancelledProbability() != 0.0)
                numFlightsToBeSetCancelled = (int) Math.round(airline.getCancelledProbability() * flights.size());

            log.info("Flights to be cancelled: " + numFlightsToBeSetCancelled);

            for (Flight flight : flights) {
                baseNumberFlights.add(flight.getFlightNumber());
            }
            log.info(String.format("base number flights %s: ", baseNumberFlights));

            for (int i = 0; i < numFlightsToBeDelayed; i++) {
                int flightNumber = getRandomFlightNumber(baseNumberFlights);
                flightsToBeSetDelayed.add(flightNumber);
            }
            delayedCancelledArrays.setDelayedFlights(flightsToBeSetDelayed);

            for (int i = 0; i < numFlightsToBeSetCancelled; i++) {
                int flightNumber = getRandomFlightNumber(baseNumberFlights);
                flightsToBeSetCancelled.add(flightNumber);
            }
            delayedCancelledArrays.setCancelledFlights(flightsToBeSetCancelled);
        }
        return delayedCancelledArrays;
    }

    private String generateTimeForResponse(int hours, int minutes) {
        return String.format("%s:%s", hours, minutes);
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

    private static int getRandomFlightNumber(ArrayList<Integer> array) {
        System.out.println(String.format("Array size: %s", array.size()));
        int rnd = new Random().nextInt(array.size() - 1);
        System.out.println(String.format("Index: %s", rnd));
        int val = array.get(rnd);
        array.remove(rnd);
        return val;
    }

    private LocalDateTime generateRandomLocalDateTime() {
        int max = 23;
        int min = 6;
        return LocalDateTime
                .of(
                        LocalDate.now(),
                        LocalTime.of(
                                r.nextInt((max - min) + 1) + min,
                                5 * (Math.round(r.nextInt(60) / 5)),
                                0,
                                0));
    }
}
