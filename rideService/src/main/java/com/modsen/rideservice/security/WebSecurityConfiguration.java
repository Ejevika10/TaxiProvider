package com.modsen.rideservice.security;

import com.modsen.rideservice.model.Role;
import com.modsen.rideservice.security.filters.CacheBodyHttpServletFilter;
import com.modsen.rideservice.security.filters.ExceptionHandlingFilter;
import com.modsen.rideservice.security.filters.RideAccessFilter;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.modsen.rideservice.util.SecurityConstants.KEYCLOAK_CLIENT_ID;
import static com.modsen.rideservice.util.SecurityConstants.TOKEN_ISSUER_URL;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final RideService rideService;

    private final CustomAuthenticationEntryPoint authEntryPoint;

    private final CustomAccessDeniedHandler accessDenied;

    @Bean
    public RideAccessFilter rideAccessFilter() {
        return new RideAccessFilter(rideService);
    }

    @Bean
    public ExceptionHandlingFilter exceptionHandlingFilter() {
        return new ExceptionHandlingFilter();
    }

    @Bean
    public CacheBodyHttpServletFilter cacheBodyHttpServletFilter() {
        return new CacheBodyHttpServletFilter();
    }

    @Value(KEYCLOAK_CLIENT_ID)
    private String kcClientId;

    @Value(TOKEN_ISSUER_URL)
    private String tokenIssuerUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new KeycloakJwtRolesConverter(kcClientId));

        http
                .addFilterBefore(exceptionHandlingFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilterAfter(cacheBodyHttpServletFilter(), ExceptionHandlingFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/actuator/health",
                                        "/ride-service/swagger-ui/**",
                                        "/ride-service/v3/**",
                                        "/ride-service/swagger-ui.html",
                                        "/ride-service/webjars/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/rides").hasAnyRole(Role.PASSENGER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/rides/{id}/accept").hasAnyRole(Role.DRIVER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/rides/{id}/cancel").hasAnyRole(Role.DRIVER.getRole(), Role.PASSENGER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/rides/{id}/state").hasAnyRole(Role.DRIVER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/rides/{id}").hasRole(Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.GET, "/api/v1/rides").hasRole(Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.GET, "/api/v1/rides/passenger/{passengerId}").hasAnyRole(Role.PASSENGER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.GET, "/api/v1/rides/driver/{driverId}").hasAnyRole(Role.DRIVER.getRole(), Role.ADMIN.getRole())
                                .requestMatchers("/api/*").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDenied)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtToken -> new JwtAuthenticationToken(jwtToken, authoritiesConverter.convert(jwtToken))
                                )
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(tokenIssuerUrl);
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
