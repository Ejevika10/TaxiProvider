package com.modsen.passengerservice.security;

import com.modsen.passengerservice.model.Role;
import com.modsen.passengerservice.security.filters.ExceptionHandlingFilter;
import com.modsen.passengerservice.security.filters.PassengerAccessFilter;
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

import static com.modsen.passengerservice.util.SecurityConstants.KEYCLOAK_CLIENT_ID;
import static com.modsen.passengerservice.util.SecurityConstants.TOKEN_ISSUER_URL;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final CustomAuthenticationEntryPoint authEntryPoint;

    private final CustomAccessDeniedHandler accessDenied;

    @Bean
    public PassengerAccessFilter passengerAccessFilter() {
        return new PassengerAccessFilter();
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new KeycloakJwtRolesConverter(kcClientId));

        http
                .addFilterBefore(exceptionHandlingFilter(), WebAsyncManagerIntegrationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/api/v1/passengers").hasRole(Role.ADMIN.getRole())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/passengers/*").hasAnyRole(Role.ADMIN.getRole(), Role.PASSENGER.getRole())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/drivers/*").hasAnyRole(Role.ADMIN.getRole(), Role.PASSENGER.getRole())
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
