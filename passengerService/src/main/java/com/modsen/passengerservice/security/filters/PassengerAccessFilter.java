package com.modsen.passengerservice.security.filters;

import com.modsen.passengerservice.exception.ForbiddenException;
import com.modsen.passengerservice.service.PassengerService;
import com.modsen.passengerservice.util.AppConstants;
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

import static com.modsen.passengerservice.util.SecurityConstants.ROLE_ADMIN;

@RequiredArgsConstructor
@Slf4j
public class PassengerAccessFilter extends OncePerRequestFilter {

    private final PassengerService passengerService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/v1/passengers")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth.getAuthorities().stream()
                    .filter(Objects::nonNull)
                    .anyMatch(authority -> authority.getAuthority().equals(ROLE_ADMIN))) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getMethod().equals("GET")) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String email = (String) claims.get("email");
            log.info(email);

            String requestURI = request.getRequestURI();
            String[] pathParts = requestURI.split("/");
            String idParam = pathParts[pathParts.length - 1];
            String passengerEmail;
            Long driverId = Long.valueOf(idParam);
            passengerEmail = passengerService.getPassengerById(driverId).email();

            log.info(passengerEmail);

            if (!Objects.equals(passengerEmail, email)) {
                throw new ForbiddenException(AppConstants.FORBIDDEN);
            }
        }
        filterChain.doFilter(request, response);
    }
}
