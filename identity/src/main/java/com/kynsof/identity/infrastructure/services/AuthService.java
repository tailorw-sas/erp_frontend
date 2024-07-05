package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.command.auth.autenticate.LoginRequest;
import com.kynsof.identity.application.command.auth.autenticate.TokenResponse;
import com.kynsof.identity.application.command.auth.forwardPassword.PasswordChangeRequest;
import com.kynsof.identity.application.command.auth.registry.UserRequest;
import com.kynsof.identity.application.command.auth.registrySystemUser.UserSystemKycloackRequest;
import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.identity.domain.interfaces.service.IRedisService;
import com.kynsof.identity.infrastructure.services.kafka.producer.ProducerTriggerPasswordResetEventService;
import com.kynsof.identity.infrastructure.services.kafka.producer.user.ProducerRegisterUserEventService;
import com.kynsof.identity.infrastructure.services.kafka.producer.user.ProducerRegisterUserSystemEventService;
import com.kynsof.share.core.domain.exception.*;
import com.kynsof.share.core.domain.kafka.entity.UserKafka;
import com.kynsof.share.core.domain.kafka.entity.UserOtpKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import io.micrometer.common.lang.NonNull;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService implements IAuthService {

    private final KeycloakProvider keycloakProvider;
    private final RestTemplate restTemplate;
    private final ProducerRegisterUserEventService producerRegisterUserEvent;
    private final IRedisService otpService;
    private final ProducerTriggerPasswordResetEventService producerOtp;

    @Autowired
    public AuthService(KeycloakProvider keycloakProvider, RestTemplate restTemplate,
                       ProducerRegisterUserEventService producerRegisterUserEvent,
                       IRedisService otpService, ProducerTriggerPasswordResetEventService producerOtp,
                       ProducerRegisterUserSystemEventService producerRegisterUserSystemEvent) {
        this.keycloakProvider = keycloakProvider;
        this.restTemplate = restTemplate;
        this.producerRegisterUserEvent = producerRegisterUserEvent;
        this.otpService = otpService;
        this.producerOtp = producerOtp;
    }

    @Override
    public TokenResponse authenticate(LoginRequest loginDTO) {
        MultiValueMap<String, String> map = createAuthRequestMap(loginDTO.getUsername(), loginDTO.getPassword());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, createHeaders());

        try {
            System.out.println(keycloakProvider.getTokenUri().toString());//djacomee
            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    keycloakProvider.getTokenUri(),
                    HttpMethod.POST,
                    entity,
                    TokenResponse.class);
            return handleAuthResponse(response);
        } catch (HttpClientErrorException e) {
            handleAuthException(e);
            return null; // Esto no se ejecutará, ya que handleAuthException lanza una excepción
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) throws CustomUnauthorizedException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakProvider.getClient_id());
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);
        map.add("client_secret", keycloakProvider.getClient_secret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, createHeaders());

        try {
            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    keycloakProvider.getTokenUri(),
                    HttpMethod.POST,
                    request,
                    TokenResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new CustomUnauthorizedException("Unauthorized: Refresh token is invalid or expired.",
                    new ErrorField("token", "Refresh token not found"));
        }
    }

    @Override
    public String registerUser(@NonNull UserRequest userRequest, boolean isSystemUser) {
        String userId = createUser(userRequest.getName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getUserName(), userRequest.getPassword());

        producerRegisterUserEvent.create(new UserKafka(
                userId,
                userRequest.getUserName(),
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getLastName(),
                "",
                "",
                "",
                ""
        ));

        return userId;
    }

    @Override
    public String registerUserSystem(@NonNull UserSystemKycloackRequest userRequest, boolean isSystemUser) {
        return createUser(userRequest.getName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getUserName(), userRequest.getPassword());
    }

    @Override
    public Boolean sendPasswordRecoveryOtp(String email) {
        UsersResource userResource = keycloakProvider.getRealmResource().users();
        List<UserRepresentation> users = userResource.searchByEmail(email, true);

        if (!users.isEmpty()) {
            UserRepresentation user = users.get(0);
            String otpCode = otpService.generateOtpCode();
            otpService.saveOtpCode(email, otpCode);
            producerOtp.create(new UserOtpKafka(email, otpCode, user.getFirstName()));
            return true;
        }
        throw new UserNotFoundException("User not found", new ErrorField("email", "Email not found"));
    }

    @Override
    public Boolean forwardPassword(PasswordChangeRequest changeRequest) {
        if (!otpService.getOtpCode(changeRequest.getEmail()).equals(changeRequest.getOtp())) {
            return false;
        }

        UsersResource userResource = keycloakProvider.getRealmResource().users();
        List<UserRepresentation> users = userResource.searchByEmail(changeRequest.getEmail(), true);
        if (!users.isEmpty()) {
            UserRepresentation user = users.get(0);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setTemporary(false);
            credential.setValue(changeRequest.getNewPassword());

            userResource.get(user.getId()).resetPassword(credential);
            return true;
        }
        throw new UserNotFoundException("User not found", new ErrorField("email/password", "Change Password not found"));
    }

    @Override
    public Boolean changePassword(String userId, String newPassword) {
        UserRepresentation user = keycloakProvider.getRealmResource().users().get(userId).toRepresentation();
        if (user != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false); // True si quieres que sea una contraseña temporal

            keycloakProvider.getRealmResource().users().get(userId).resetPassword(credential);
            return true;
        }
        throw new UserNotFoundException("User not found", new ErrorField("email/password", "Change Password not found"));
    }

    @Override
    public Boolean firstChangePassword(String userId, String email, String newPassword, String oldPassword) {
        try {
            LoginRequest loginDTO = new LoginRequest(email, oldPassword);
            authenticate(loginDTO);
        } catch (UserChangePasswordException exception) {
            changePassword(userId, newPassword);
            return true;
        }
        return false;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> createAuthRequestMap(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakProvider.getClient_id());
        map.add("grant_type", keycloakProvider.getGrant_type());
        map.add("username", username);
        map.add("password", password);
        map.add("client_secret", keycloakProvider.getClient_secret());
        return map;
    }

    private TokenResponse handleAuthResponse(ResponseEntity<TokenResponse> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new AuthenticateNotFoundException("The username or password is incorrect. Please try again.",
                    new ErrorField("email/password", "The username or password is incorrect. Please try again."));
        }
    }

    private void handleAuthException(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorResponse = e.getResponseBodyAsString();
            if (errorResponse.contains("invalid_grant")) {
                throw new UserChangePasswordException("You must change your password before continuing.",
                        new ErrorField("password", "You must change your password before continuing."));
            }
        }
        throw new AuthenticateNotFoundException("The username or password is incorrect. Please try again.", new ErrorField("email/password", "The username or password is incorrect. Please try again."));
    }

    private String createUser(String firstName, String lastName, String email, String username, String password) {
        UsersResource usersResource = keycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(email);
        userRepresentation.setUsername(username);
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        Response response = usersResource.create(userRepresentation);

        if (response.getStatus() == 201) {
            String userId = extractUserIdFromLocation(response.getLocation().getPath());
            setNewUserPassword(password, userId, usersResource);
            return userId;
        } else if (response.getStatus() == 409) {
            throw new AlreadyExistsException("User already exists", new ErrorField("email", "Email is already in use"));
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    private String extractUserIdFromLocation(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void setNewUserPassword(String password, String userId, UsersResource usersResource) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(true);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        usersResource.get(userId).resetPassword(credential);
    }

    private void assignRolesToUser(List<String> roles, String userId) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        UsersResource usersResource = keycloakProvider.getUserResource();
        RealmResource realmResource = keycloakProvider.getRealmResource();
        String clientId = realmResource.clients().findByClientId(keycloakProvider.getClient_id()).get(0).getId();
        ClientResource clientResource = realmResource.clients().get(clientId);
        List<RoleRepresentation> roleRepresentations = new ArrayList<>();

        RolesResource rolesResource = clientResource.roles();
        for (String roleName : roles) {
            RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
            roleRepresentations.add(role);
        }

        if (!roleRepresentations.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().add(roleRepresentations);
        }
    }
}
