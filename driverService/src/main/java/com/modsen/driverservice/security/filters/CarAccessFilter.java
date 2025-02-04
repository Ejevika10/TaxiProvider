package com.modsen.driverservice.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.exception.ForbiddenException;
import com.modsen.driverservice.service.CarService;
import com.modsen.driverservice.util.AppConstants;
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

import static com.modsen.driverservice.util.SecurityConstants.ROLE_ADMIN;

@RequiredArgsConstructor
@Slf4j
public class CarAccessFilter extends OncePerRequestFilter {

    private final CarService carService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/v1/cars")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (hasRole(auth, ROLE_ADMIN)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getMethod().equals("GET")) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String userIdParam = (String) claims.get("user_id");
            log.info(userIdParam);
            UUID userId = UUID.fromString(userIdParam);

            if (request.getMethod().equals("POST")) {
                CarRequestDto carRequestDto = getCarRequestDto(request);
                UUID driverId = UUID.fromString(carRequestDto.driverId());
                if (!Objects.equals(userId, driverId)) {
                    throw new ForbiddenException(AppConstants.FORBIDDEN);
                }
            }
            else {
                UUID carOwnerId = getCarOwnerIdFromRequestURI(request.getRequestURI());
                if (!Objects.equals(userId, carOwnerId)) {
                    throw new ForbiddenException(AppConstants.FORBIDDEN);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private UUID getCarOwnerIdFromRequestURI(String requestURI) {
        String[] pathParts = requestURI.split("/");
        String idParam = pathParts[pathParts.length - 1];
        Long carId = Long.valueOf(idParam);
        return carService.getCarById(carId).driver().id();
    }

    private CarRequestDto getCarRequestDto(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return objectMapper.readValue(body, CarRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
