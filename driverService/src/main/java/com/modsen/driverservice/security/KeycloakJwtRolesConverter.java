package com.modsen.driverservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static com.modsen.driverservice.util.SecurityConstants.CLAIM_REALM_ACCESS;
import static com.modsen.driverservice.util.SecurityConstants.CLAIM_RESOURCE_ACCESS;
import static com.modsen.driverservice.util.SecurityConstants.CLAIM_ROLES;

@Slf4j
public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final String kcClientId;

    public KeycloakJwtRolesConverter(String kcClientId) {
        this.kcClientId = kcClientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        log.info(jwt.toString());
        Map<String, Collection<String>> realmAccess = jwt.getClaim(CLAIM_REALM_ACCESS);
        Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim(CLAIM_RESOURCE_ACCESS);

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (realmAccess != null && !realmAccess.isEmpty()) {
            realmAccess.get(CLAIM_ROLES).forEach(r -> {
                String role = r.toUpperCase(Locale.ROOT);
                log.info(role);
                grantedAuthorities.add(new SimpleGrantedAuthority(role));

            });
        }

        if (resourceAccess != null && !resourceAccess.isEmpty() && resourceAccess.containsKey(kcClientId)) {
            resourceAccess.get(kcClientId).get(CLAIM_ROLES).forEach(r -> {
                String role = r.toUpperCase(Locale.ROOT);
                log.info(role);
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            });
        }

        return grantedAuthorities;
    }
}
