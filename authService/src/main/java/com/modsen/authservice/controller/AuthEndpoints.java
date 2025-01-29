package com.modsen.authservice.controller;

import com.modsen.authservice.dto.DriverResponseDto;
import com.modsen.authservice.dto.LoginRequestDto;
import com.modsen.authservice.dto.LoginResponseDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import com.modsen.authservice.dto.RegisterRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Validated
@Tag(
        name = "Authorization controller",
        description = """
                The authorization controller handles login requests and generates access tokens,
                provides functionality to refresh access tokens using a valid refresh token,
                allows new users to register
                """
)
public interface AuthEndpoints {
    @Operation(
            summary = "User login",
            description = """
                    Authenticates a user with their credentials and returns an access token.
                    The access token can be used for subsequent requests to access protected resources.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You are logged in"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "User with this login and password doesn`t exist"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable"),
    })
    LoginResponseDto login (HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse,
                                   @Parameter(description = "Login data", required = true)
                                    @Valid @RequestBody LoginRequestDto loginRequest);

    @Operation(
            summary = "Refresh access token",
            description = """
                    Allows users to obtain a new access token by providing a valid refresh token.
                    The new access token will be returned if the refresh token is valid and not expired.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You are logged in"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "User with this login and password doesn`t exist"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable"),
    })
    LoginResponseDto refreshToken (HttpServletRequest servletRequest,
                                          HttpServletResponse servletResponse,
                                          @RequestHeader("refresh_token") String refreshToken);

    @Operation(
            summary = "Register driver",
            description = """
                    Allows new driver to register for an account in the application.
                    Upon successful registration, an account will be created in the system,
                    and the user will receive an account info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You are logged in"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "409", description = "Your request is invalid"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable"),
    })
    DriverResponseDto registerDriver (HttpServletRequest servletRequest,
                                             HttpServletResponse servletResponse,
                                             @Valid @RequestBody RegisterRequestDto registerRequestDto);

    @Operation(
            summary = "Register passenger",
            description = """
                    Allows new passenger to register for an account in the application.
                    Upon successful registration, an account will be created in the system,
                    and the user will receive an account info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You are logged in"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "409", description = "Your request is invalid"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable"),
    })
    PassengerResponseDto registerPassenger (HttpServletRequest servletRequest,
                                                   HttpServletResponse servletResponse,
                                                   @Valid @RequestBody RegisterRequestDto registerRequestDto);

}
