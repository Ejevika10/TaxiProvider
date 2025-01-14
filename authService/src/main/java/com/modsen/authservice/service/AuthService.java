package com.modsen.authservice.service;

import com.modsen.authservice.dto.DriverResponseDto;
import com.modsen.authservice.dto.LoginRequestDto;
import com.modsen.authservice.dto.LoginResponseDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import com.modsen.authservice.dto.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request, HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    LoginResponseDto refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse);


    DriverResponseDto registerDriver(RegisterRequestDto request, HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    PassengerResponseDto registerPassenger(RegisterRequestDto request, HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
