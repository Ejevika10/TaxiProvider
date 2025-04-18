package com.modsen.rideservice.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.exception.ForbiddenException;
import com.modsen.exceptionstarter.exception.RequestBodyReadException;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.model.Role;
import com.modsen.rideservice.service.RideService;
import com.modsen.rideservice.util.MessageConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class RideAccessFilter extends OncePerRequestFilter {

    private final RideService rideService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/v1/rides")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (hasRole(auth, Role.ADMIN.getRole())) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String userIdParam = (String) claims.get("user_id");
            log.info(userIdParam);
            UUID userId = UUID.fromString(userIdParam);

            if (request.getMethod().equals("GET")) {
                if (isUserRequest(request.getRequestURI())) {
                    UUID userIdFromRequestURI = getUserIdFromRequestURI(request.getRequestURI());
                    if (!Objects.equals(userId, userIdFromRequestURI)) {
                        throw new ForbiddenException(MessageConstants.FORBIDDEN);
                    }
                }
                filterChain.doFilter(request, response);
                return;
            }

            if(request.getRequestURI().endsWith("accept")) {
                RideAcceptRequestDto rideAcceptRequestDto = getRideAcceptRequestDto(request);
                UUID driverId = UUID.fromString(rideAcceptRequestDto.driverId());
                if (!Objects.equals(userId, driverId)) {
                    throw new ForbiddenException(MessageConstants.FORBIDDEN);
                }
            }
            else if(request.getRequestURI().endsWith("cancel")) {
                if (hasRole(auth, Role.DRIVER.getRole())) {
                    UUID driverId = getDriverIdFromStatusRequestURI(request.getRequestURI());
                    if (!Objects.equals(userId, driverId)) {
                        throw new ForbiddenException(MessageConstants.FORBIDDEN);
                    }
                }
                else if (hasRole(auth, Role.PASSENGER.getRole())) {
                    UUID passengerId = getPassengerIdFromStatusRequestURI(request.getRequestURI());
                    if (!Objects.equals(userId, passengerId)) {
                        throw new ForbiddenException(MessageConstants.FORBIDDEN);
                    }
                }
            }
            else if(request.getRequestURI().endsWith("state")) {
                UUID driverId = getDriverIdFromStatusRequestURI(request.getRequestURI());
                if (!Objects.equals(userId, driverId)) {
                    throw new ForbiddenException(MessageConstants.FORBIDDEN);
                }
            }
            else {
                RideCreateRequestDto rideCreateRequestDto = getRideCreateRequestDto(request);
                log.info(rideCreateRequestDto.toString());
                UUID passengerId = UUID.fromString(rideCreateRequestDto.passengerId());
                if(!Objects.equals(userId, passengerId)) {
                    throw new ForbiddenException(MessageConstants.FORBIDDEN);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private RideCreateRequestDto getRideCreateRequestDto(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return objectMapper.readValue(body, RideCreateRequestDto.class);
        } catch (IOException e) {
            throw new RequestBodyReadException(MessageConstants.BODY_READ_ERROR);
        }
    }

    private RideAcceptRequestDto getRideAcceptRequestDto(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return objectMapper.readValue(body, RideAcceptRequestDto.class);
        } catch (IOException e) {
            throw new RequestBodyReadException(MessageConstants.BODY_READ_ERROR);
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private UUID getDriverIdFromStatusRequestURI(String requestURI) {
        String[] pathParts = requestURI.split("/");
        String idParam = pathParts[pathParts.length - 2];
        Long rideId = Long.valueOf(idParam);
        return rideService.getRideById(rideId).driverId();
    }

    private UUID getPassengerIdFromStatusRequestURI(String requestURI) {
        String[] pathParts = requestURI.split("/");
        String idParam = pathParts[pathParts.length - 2];
        Long rideId = Long.valueOf(idParam);
        return rideService.getRideById(rideId).passengerId();
    }

    private UUID getUserIdFromRequestURI(String requestURI) {
        String[] pathParts = requestURI.split("/");
        String idParam = pathParts[pathParts.length - 1];
        return UUID.fromString(idParam);
    }

    private boolean isUserRequest(String requestURI) {
        String[] pathParts = requestURI.split("/");
        return pathParts[pathParts.length - 2].equals("passenger")
                || pathParts[pathParts.length - 2].equals("driver");
    }
}
