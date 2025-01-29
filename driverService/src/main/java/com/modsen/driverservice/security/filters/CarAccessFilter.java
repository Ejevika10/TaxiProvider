package com.modsen.driverservice.security.filters;

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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.modsen.driverservice.util.SecurityConstants.ROLE_ADMIN;

@RequiredArgsConstructor
@Slf4j
public class CarAccessFilter extends OncePerRequestFilter {

    private final CarService carService;

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
            UUID carOwnerId = getCarOwnerIdFromRequestURI(request.getRequestURI());
            if (!Objects.equals(userId, carOwnerId)) {
                throw new ForbiddenException(AppConstants.FORBIDDEN);
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
}
