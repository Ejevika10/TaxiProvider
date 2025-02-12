package com.modsen.ratingservice.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exceptionstarter.exception.ForbiddenException;
import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.model.Role;
import com.modsen.ratingservice.util.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class DriverRatingsAccessFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RideClientService rideClientService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/v1/driverratings")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (hasRole(auth, Role.ADMIN.getRole())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getMethod().equals("GET")) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String id = (String) claims.get("user_id");
            UUID userId = UUID.fromString(id);

            RatingRequestDto ratingRequestDto = getRatingRequestDto(request);
            RideResponseDto ride = getRideById(ratingRequestDto.rideId(), jwtAuth.getName());

            if(!ride.passengerId().equals(userId)) {
                throw new ForbiddenException(AppConstants.FORBIDDEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private RatingRequestDto getRatingRequestDto(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return objectMapper.readValue(body, RatingRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private RideResponseDto getRideById(Long rideId, String accessToken) {
        return rideClientService.getRideById(rideId, accessToken);
    }
}
