package com.modsen.reportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.modsen.reportservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Report controller",
        description = """
                The report controller provides an ability
                to get a month report about driver activity
                by driver id
                """
)
public interface ReportEndpoints {
    @Operation(
            summary = "Get a month report by driverId",
            description = "Allows users to get a month report by driverId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist")
    })
    ResponseEntity<Resource> getReport(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String driverId,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
