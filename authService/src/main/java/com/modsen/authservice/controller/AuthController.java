package com.modsen.authservice.controller;

import com.modsen.authservice.dto.DriverResponseDto;
import com.modsen.authservice.dto.LoginRequestDto;
import com.modsen.authservice.dto.LoginResponseDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import com.modsen.authservice.dto.RegisterRequestDto;
import com.modsen.authservice.dto.UpdatePasswordRequestDto;
import com.modsen.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthEndpoints {
    private final AuthService authService;

    @Override
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto login (HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse,
                                   @Valid @RequestBody LoginRequestDto loginRequest) {

        return authService.login(loginRequest, servletRequest, servletResponse);
    }

    @Override
    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto refreshToken (HttpServletRequest servletRequest,
                                          HttpServletResponse servletResponse,
                                          @RequestHeader("refresh_token") String refreshToken) {

        return authService.refreshToken(servletRequest, servletResponse);
    }

    @Override
    @PostMapping("/register-driver")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponseDto registerDriver (HttpServletRequest servletRequest,
                                             HttpServletResponse servletResponse,
                                             @Valid @RequestBody RegisterRequestDto registerRequestDto) {

        return authService.registerDriver(registerRequestDto, servletRequest, servletResponse);
    }

    @Override
    @PostMapping("/register-passenger")
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponseDto registerPassenger (HttpServletRequest servletRequest,
                                                   HttpServletResponse servletResponse,
                                                   @Valid @RequestBody RegisterRequestDto registerRequestDto) {

        return authService.registerPassenger(registerRequestDto, servletRequest, servletResponse);
    }

    @PutMapping("/password/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword (HttpServletRequest servletRequest,
                                HttpServletResponse servletResponse,
                                @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto,
                                @PathVariable String userId) {

        authService.updatePassword(userId, updatePasswordRequestDto);
    }
}
