package com.modsen.ratingservice.security;

import com.modsen.ratingservice.security.filters.DriverRatingsAccessFilter;
import com.modsen.ratingservice.security.filters.ExceptionHandlingFilter;
import com.modsen.ratingservice.security.filters.PassengerRatingsAccessFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import static com.modsen.ratingservice.util.SecurityConstants.KEYCLOAK_CLIENT_ID;
import static com.modsen.ratingservice.util.SecurityConstants.ROLE_PASSENGER;
import static com.modsen.ratingservice.util.SecurityConstants.TOKEN_ISSUER_URL;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final CustomAuthenticationEntryPoint authEntryPoint;

    private final CustomAccessDenied accessDenied;

    @Bean
    public DriverRatingsAccessFilter driverRatingsAccessFilter() {
        return new DriverRatingsAccessFilter();
    }

    @Bean
    public PassengerRatingsAccessFilter passengerRatingsAccessFilter() {
        return new PassengerRatingsAccessFilter();
    }

    @Bean
    public ExceptionHandlingFilter exceptionHandlingFilter() {
        return new ExceptionHandlingFilter();
    }

    @Value(KEYCLOAK_CLIENT_ID)
    private String kcClientId;

    @Value(TOKEN_ISSUER_URL)
    private String tokenIssuerUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new KeycloakJwtRolesConverter(kcClientId));

        http
                .addFilterBefore(exceptionHandlingFilter(), WebAsyncManagerIntegrationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/actuator/health").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/rides").hasRole(ROLE_PASSENGER)
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDenied)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtToken -> new JwtAuthenticationToken(jwtToken, authoritiesConverter.convert(jwtToken))
                                )
                        )
                );

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
