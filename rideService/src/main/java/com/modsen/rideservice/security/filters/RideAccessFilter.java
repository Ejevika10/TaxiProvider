package com.modsen.rideservice.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.dto.RideRequestDto;
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

import static com.modsen.rideservice.util.SecurityConstants.ROLE_ADMIN;
import static com.modsen.rideservice.util.SecurityConstants.ROLE_PASSENGER;

@RequiredArgsConstructor
@Slf4j
public class RideAccessFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/v1/rides")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (hasRole(auth, ROLE_ADMIN)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getMethod().equals("GET")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getMethod().equals("POST") &&
                    hasRole(auth, ROLE_PASSENGER)) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String id = (String) claims.get("user_id");
            log.info(id);

            RideRequestDto rideRequestDto = getRideRequestDto(request);
            log.info(rideRequestDto.toString());

            // To Do:здесь нужна нормальная проверка id

            /*if (hasRole(auth, ROLE_PASSENGER) &&
                    !Objects.equals(rideRequestDto.passengerId(), id)) {
                throw new ForbiddenException(AppConstants.FORBIDDEN);
            }
            if (hasRole(auth, ROLE_DRIVER) &&
                    !Objects.equals(rideRequestDto.driverId(), id)) {
                throw new ForbiddenException(AppConstants.FORBIDDEN);
            }*/
        }
        filterChain.doFilter(request, response);
    }

    private RideRequestDto getRideRequestDto(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return objectMapper.readValue(body, RideRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
