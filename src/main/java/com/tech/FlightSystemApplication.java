package com.tech;

import com.tech.model.Airport;
import com.tech.repository.AirportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@EnableJpaAuditing
@SpringBootApplication
public class FlightSystemApplication {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    public static void main(String[] args) {
        SpringApplication.run(FlightSystemApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/swagger"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }

    @Bean
    public CommandLineRunner mappingDemo(AirportRepository airportRepository) {
        final Logger log = LoggerFactory.getLogger(FlightSystemApplication.class);
        return args -> {

            log.info("********* populating airports table *********");

            Airport fiumicino = airportRepository.save(
                    new Airport(
                            "FCO",
                            "Fiumicino International Airport Leonardo Da Vinci",
                            "Rome",
                            "Italy"));
            log.info(String.format("+===> airport %s created", fiumicino.getIataCode()));

            Airport ny = airportRepository.save(
                    new Airport(
                            "JFK",
                            "John F. Kennedy International Airport",
                            "New York",
                            "United States"));
            log.info(String.format("+===> airport %s created", ny.getIataCode()));

            Airport schipol = airportRepository.save(
                    new Airport(
                            "AMS",
                            "Schipol Airport",
                            "Amsterdam",
                            "Netherlands"));
            log.info(String.format("+===> airport %s created", schipol.getIataCode()));

            Airport orly = airportRepository.save(
                    new Airport(
                            "ORY",
                            "Orly Airport",
                            "Paris",
                            "France"));
            log.info(String.format("+===> airport %s created", orly.getIataCode()));

            Airport brisbane = airportRepository.save(
                    new Airport(
                            "BNE",
                            "Brisbane Airport",
                            "Brisbane",
                            "Australia"));
            log.info(String.format("+===> airport %s created", brisbane.getIataCode()));

            Airport kannur = airportRepository.save(
                    new Airport(
                            "CNN",
                            "Kannur Airport",
                            "Kannur",
                            "India"));
            log.info(String.format("+===> airport %s created", kannur.getIataCode()));

            log.info("********* airports table population over *********");
        };
    }

}
