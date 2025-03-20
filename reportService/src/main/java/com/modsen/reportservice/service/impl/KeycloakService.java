package com.modsen.reportservice.service.impl;

import com.modsen.exceptionstarter.exception.ServiceUnavailableException;
import com.modsen.reportservice.configuration.KeycloakProperties;
import com.modsen.reportservice.dto.LoginResponseDto;
import com.modsen.reportservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate;
    private final KeycloakProperties keycloakProperties;

    public String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS);
        requestBody.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
        requestBody.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());

        try {
            ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity (keycloakProperties.getGetTokenUrl(),
                    new HttpEntity<>(requestBody, headers), LoginResponseDto.class);
            return response.getBody().accessToken();
        } catch (HttpClientErrorException e) {
            throw new ServiceUnavailableException(MessageConstants.SERVICE_UNAVAILABLE);
        }
    }
}
