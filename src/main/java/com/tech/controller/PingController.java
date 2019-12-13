package com.tech.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@Api(value="Ping Controller request")
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    private static void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }


    @ApiOperation(value = "Ping request", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully pong"),
            @ApiResponse(code = 400, message = "Service broken")
    })
    @GetMapping(value = "/ping")
    public String ping() {
        String transactionId = UUID.randomUUID().toString();
        logInfoWithTransactionId(
                transactionId,
                "Got new ping request"
        );

        return "pong";
    }

}
