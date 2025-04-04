package com.modsen.reportservice.security.filters;

import com.modsen.reportservice.model.Role;
import com.modsen.reportservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.ForbiddenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ReportAccessFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/v1/drivers")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (hasRole(auth, Role.ADMIN.getRole())) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            String userIdParam = (String) claims.get("user_id");
            UUID userId = UUID.fromString(userIdParam);
            UUID driverId = getDriverIdFromReportRequestURI(request.getRequestURI());
            if (!Objects.equals(driverId, userId)) {
                throw new ForbiddenException(MessageConstants.FORBIDDEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private UUID getDriverIdFromReportRequestURI(String requestURI) {
        String[] pathParts = requestURI.split("/");
        String idParam = pathParts[pathParts.length - 1];
        return UUID.fromString(idParam);
    }
}
