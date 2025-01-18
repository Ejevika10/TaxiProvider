package com.modsen.authservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.authservice.client.driver.DriverClientService;
import com.modsen.authservice.client.passenger.PassengerClientService;
import com.modsen.authservice.dto.DriverCreateRequestDto;
import com.modsen.authservice.dto.DriverResponseDto;
import com.modsen.authservice.dto.LoginRequestDto;
import com.modsen.authservice.dto.LoginResponseDto;
import com.modsen.authservice.dto.PassengerCreateRequestDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import com.modsen.authservice.dto.RegisterRequestDto;
import com.modsen.authservice.exception.ErrorMessage;
import com.modsen.authservice.exception.KeycloakException;
import com.modsen.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import static com.modsen.authservice.util.AppConstants.ACCESS_TOKEN;
import static com.modsen.authservice.util.AppConstants.CLIENT_ID_PARAM;
import static com.modsen.authservice.util.AppConstants.CLIENT_SECRET_PARAM;
import static com.modsen.authservice.util.AppConstants.ERROR_MESSAGE_FIELD;
import static com.modsen.authservice.util.AppConstants.GRANT_TYPE_PARAM;
import static com.modsen.authservice.util.AppConstants.PASSWORD_PARAM;
import static com.modsen.authservice.util.AppConstants.REFRESH_TOKEN_PARAM;
import static com.modsen.authservice.util.AppConstants.UNKNOWN_ERROR;
import static com.modsen.authservice.util.AppConstants.USERNAME_PARAM;
import static com.modsen.authservice.util.KeycloakConstants.CLIENT_ID;
import static com.modsen.authservice.util.KeycloakConstants.CLIENT_SECRET;
import static com.modsen.authservice.util.AppConstants.EXPIRES_IN;
import static com.modsen.authservice.util.KeycloakConstants.DRIVER_ROLE;
import static com.modsen.authservice.util.KeycloakConstants.GET_TOKEN_URL;
import static com.modsen.authservice.util.KeycloakConstants.GRANT_TYPE_PASSWORD;
import static com.modsen.authservice.util.KeycloakConstants.GRANT_TYPE_REFRESH_TOKEN;
import static com.modsen.authservice.util.KeycloakConstants.PASSENGER_ROLE;
import static com.modsen.authservice.util.KeycloakConstants.REALM_NAME;
import static com.modsen.authservice.util.AppConstants.REFRESH_TOKEN;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;
    private final Keycloak keycloak;
    private final PassengerClientService passengerClientService;
    private final DriverClientService driverClientService;


    @Override
    public LoginResponseDto login(LoginRequestDto request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LoginResponseDto responseDto = getAccessToken(request);

        servletResponse.addHeader(ACCESS_TOKEN, responseDto.access_token());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(responseDto.expires_in()));

        return responseDto;
    }

    private LoginResponseDto getAccessToken(LoginRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD);
        requestBody.add(CLIENT_ID_PARAM, CLIENT_ID);
        requestBody.add(CLIENT_SECRET_PARAM, CLIENT_SECRET);
        requestBody.add(USERNAME_PARAM, request.username());
        requestBody.add(PASSWORD_PARAM, request.password());
        log.info(GET_TOKEN_URL);
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity (GET_TOKEN_URL,
                new HttpEntity<>(requestBody, headers), LoginResponseDto.class);

        return response.getBody();
    }

    @Override
    public LoginResponseDto refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String refreshToken = servletRequest.getHeader(REFRESH_TOKEN);
        LoginResponseDto responseDto = getRefreshToken(refreshToken);

        servletResponse.addHeader(ACCESS_TOKEN, responseDto.access_token());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(responseDto.expires_in()));

        return responseDto;
    }

    private LoginResponseDto getRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(GRANT_TYPE_PARAM, GRANT_TYPE_REFRESH_TOKEN);
        requestBody.add(REFRESH_TOKEN_PARAM, refreshToken);
        requestBody.add(CLIENT_ID_PARAM, CLIENT_ID);
        requestBody.add(CLIENT_SECRET_PARAM, CLIENT_SECRET);

        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity (GET_TOKEN_URL,
                new HttpEntity<>(requestBody, headers), LoginResponseDto.class);

        return response.getBody();
    }

    @Override
    public DriverResponseDto registerDriver(RegisterRequestDto request,
                                            HttpServletRequest servletRequest,
                                            HttpServletResponse servletResponse) {
        UserRepresentation user = getUserRepresentation(request);

        RealmResource realmResource = keycloak.realm(REALM_NAME);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if(response.getStatus() == HttpStatus.CREATED.value()) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource createdUser = realmResource.users().get(userId);
            RoleRepresentation role = realmResource.roles().get(DRIVER_ROLE).toRepresentation();
            createdUser.roles().realmLevel().add(List.of(role));

            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                return createDriver(userId, request, adminClientAccessToken);
            }
            catch (Exception e) {
                usersResource.delete(userId);
                throw e;
            }
        }
        else {
            throw new KeycloakException(
                    new ErrorMessage(response.getStatus(), getErrorMessage(response))
            );
        }
    }

    private DriverResponseDto createDriver(String userId, RegisterRequestDto request, String adminClientAccessToken) {
        DriverCreateRequestDto driverRequestDto = new DriverCreateRequestDto(userId, request.username(), request.email(), 0.0, request.phone());
        return driverClientService.createDriver(driverRequestDto, "Bearer " + adminClientAccessToken);
    }

    @Override
    public PassengerResponseDto registerPassenger(RegisterRequestDto request,
                                                  HttpServletRequest servletRequest,
                                                  HttpServletResponse servletResponse) {
        UserRepresentation user = getUserRepresentation(request);

        RealmResource realmResource = keycloak.realm(REALM_NAME);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if(response.getStatus() == HttpStatus.CREATED.value()) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource createdUser = realmResource.users().get(userId);

            RoleRepresentation role = realmResource.roles().get(PASSENGER_ROLE).toRepresentation();
            createdUser.roles().realmLevel().add(List.of(role));

            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                return createPassenger(userId, request, adminClientAccessToken);
            }
            catch (Exception e) {
                usersResource.delete(userId);
                throw e;
            }

        }
        else {
            throw new KeycloakException(
                    new ErrorMessage(response.getStatus(), getErrorMessage(response))
            );
        }
    }

    private PassengerResponseDto createPassenger(String userId, RegisterRequestDto request, String adminClientAccessToken) {
        PassengerCreateRequestDto passengerRequestDto = new PassengerCreateRequestDto(userId, request.username(), request.email(), request.phone(), 0.0);
        return passengerClientService.createPassenger(passengerRequestDto, "Bearer " + adminClientAccessToken);
    }

    private static UserRepresentation getUserRepresentation(RegisterRequestDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEmailVerified(true);
        user.setAttributes(Collections.singletonMap("phone", Collections.singletonList(request.phone())));
        user.setEnabled(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setValue(request.password());
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(List.of(passwordCred));
        return user;
    }

    private static String getErrorMessage(Response response) {
        String entity = response.readEntity(String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(entity);
            return jsonNode.get(ERROR_MESSAGE_FIELD).asText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return UNKNOWN_ERROR;
    }
}
