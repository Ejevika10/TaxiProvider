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
import com.modsen.authservice.dto.UpdatePasswordRequestDto;
import com.modsen.authservice.dto.UserDeleteRequestDto;
import com.modsen.authservice.dto.UserUpdateRequestDto;
import com.modsen.authservice.model.Role;
import com.modsen.authservice.service.AuthService;
import com.modsen.authservice.configuration.KeycloakProperties;
import com.modsen.authservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.exceptionstarter.exception.KeycloakException;
import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.exceptionstarter.message.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import static com.modsen.authservice.util.MessageConstants.ERROR_MESSAGE_FIELD;
import static com.modsen.authservice.util.MessageConstants.UNKNOWN_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;
    private final Keycloak keycloak;
    private final PassengerClientService passengerClientService;
    private final DriverClientService driverClientService;
    private final KeycloakProperties keycloakProperties;

    @Override
    public LoginResponseDto login(LoginRequestDto request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LoginResponseDto responseDto = getAccessToken(request.username(), request.password());

        servletResponse.addHeader(OAuth2Constants.ACCESS_TOKEN, responseDto.accessToken());
        servletResponse.addHeader(OAuth2Constants.EXPIRES_IN, String.valueOf(responseDto.expiresIn()));

        return responseDto;
    }

    private LoginResponseDto getAccessToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        requestBody.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
        requestBody.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
        requestBody.add(OAuth2Constants.USERNAME, username);
        requestBody.add(OAuth2Constants.PASSWORD, password);
        try {
            ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity (keycloakProperties.getGetTokenUrl(),
                    new HttpEntity<>(requestBody, headers), LoginResponseDto.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new NotFoundException(MessageConstants.USER_DOESNT_EXIST);
        }
    }

    @Override
    public LoginResponseDto refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String refreshToken = servletRequest.getHeader(OAuth2Constants.REFRESH_TOKEN);
        LoginResponseDto responseDto = getRefreshToken(refreshToken);

        servletResponse.addHeader(OAuth2Constants.ACCESS_TOKEN, responseDto.accessToken());
        servletResponse.addHeader(OAuth2Constants.EXPIRES_IN, String.valueOf(responseDto.expiresIn()));

        return responseDto;
    }

    private LoginResponseDto getRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
        requestBody.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);
        requestBody.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
        requestBody.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
        try {
            ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity (keycloakProperties.getGetTokenUrl(),
                    new HttpEntity<>(requestBody, headers), LoginResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            throw new InvalidFieldValueException(MessageConstants.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public DriverResponseDto registerDriver(RegisterRequestDto request,
                                            HttpServletRequest servletRequest,
                                            HttpServletResponse servletResponse) {
        UserRepresentation user = getRegisterUserRepresentation(request);

        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealmName());
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if(response.getStatus() == HttpStatus.CREATED.value()) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource createdUser = realmResource.users().get(userId);
            RoleRepresentation role = realmResource.roles().get(Role.DRIVER.getRole()).toRepresentation();
            createdUser.roles().realmLevel().add(List.of(role));

            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                return createDriver(userId, request, adminClientAccessToken);
            } catch (Exception e) {
                usersResource.delete(userId);
                throw e;
            }
        }
        else {
            throw new KeycloakException(
                    new ErrorMessage(response.getStatus(), getErrorMessage(
                            response.readEntity(String.class)))
            );
        }
    }

    private DriverResponseDto createDriver(String userId, RegisterRequestDto request, String adminClientAccessToken) {
        DriverCreateRequestDto driverRequestDto = new DriverCreateRequestDto(userId, request.username(), request.email(), 0.0, request.phone());
        return driverClientService.createDriver(driverRequestDto, "Bearer " + adminClientAccessToken);
    }

    public void updateUser(UserUpdateRequestDto updateRequestDto) {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealmName());
        UsersResource usersResource = realmResource.users();
        UserResource user = usersResource.get(updateRequestDto.id());
        UserRepresentation newUser = getUpdateUserRepresentation(updateRequestDto);
        user.update(newUser);
    }

    public void deleteUser(UserDeleteRequestDto userDeleteRequestDto) {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealmName());
        UsersResource usersResource = realmResource.users();
        usersResource.get(userDeleteRequestDto.id()).remove();
    }

    @Override
    public void updatePassword(String id, UpdatePasswordRequestDto updatePasswordRequestDto) {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealmName());
        UsersResource usersResource = realmResource.users();
        UserResource user = usersResource.get(id);
        UserRepresentation userRepresentation = user.toRepresentation();

        LoginResponseDto response = getAccessToken(userRepresentation.getUsername(), updatePasswordRequestDto.oldPassword());
        user.resetPassword(getNewCredentials(updatePasswordRequestDto.newPassword()));
    }

    @Override
    public PassengerResponseDto registerPassenger(RegisterRequestDto request,
                                                  HttpServletRequest servletRequest,
                                                  HttpServletResponse servletResponse) {
        UserRepresentation user = getRegisterUserRepresentation(request);

        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealmName());
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if(response.getStatus() == HttpStatus.CREATED.value()) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource createdUser = realmResource.users().get(userId);

            RoleRepresentation role = realmResource.roles().get(Role.PASSENGER.getRole()).toRepresentation();
            createdUser.roles().realmLevel().add(List.of(role));

            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                return createPassenger(userId, request, adminClientAccessToken);
            } catch (Exception e) {
                usersResource.delete(userId);
                log.error(e.getMessage());
                throw e;
            }
        }
        else {
            throw new KeycloakException(
                    new ErrorMessage(response.getStatus(), getErrorMessage(
                            response.readEntity(String.class)))
            );
        }
    }

    private PassengerResponseDto createPassenger(String userId, RegisterRequestDto request, String adminClientAccessToken) {
        PassengerCreateRequestDto passengerRequestDto = new PassengerCreateRequestDto(userId, request.username(), request.email(), request.phone(), 0.0);
        return passengerClientService.createPassenger(passengerRequestDto, "Bearer " + adminClientAccessToken);
    }

    private static UserRepresentation getRegisterUserRepresentation(RegisterRequestDto request) {
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

    private static UserRepresentation getUpdateUserRepresentation(UserUpdateRequestDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEmailVerified(true);
        user.setAttributes(Collections.singletonMap("phone", Collections.singletonList(request.phone())));
        user.setEnabled(true);

        return user;
    }

    private static CredentialRepresentation getNewCredentials(String newPassword) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setValue(newPassword);
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        return passwordCred;
    }

    private static String getErrorMessage(String exception) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(exception);
            return jsonNode.get(ERROR_MESSAGE_FIELD).asText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return UNKNOWN_ERROR;
    }
}
