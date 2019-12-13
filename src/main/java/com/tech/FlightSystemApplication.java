package com.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@EnableJpaAuditing
@SpringBootApplication
public class FlightSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightSystemApplication.class, args);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction() {
		return route(GET("/swagger"), req ->
				ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
	}

}
