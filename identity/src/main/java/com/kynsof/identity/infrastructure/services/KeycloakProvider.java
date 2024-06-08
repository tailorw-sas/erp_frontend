package com.kynsof.identity.infrastructure.services;

import lombok.Getter;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KeycloakProvider {

    @Value("${keycloak.provider.server-url:https://auth.chevere.ddns.net/}")
    private String server_url;

    @Value("${keycloak.provider.realm-name:kynsoft}")
    private String realm_name;

    @Value("${keycloak.provider.realm-master:master}")
    private String realm_master;

    @Value("${keycloak.provider.admin-clic:admin-cli}")
    private String admin_clic;

    @Value("${keycloak.provider.user-console:admin}")
    private String user_console;

    @Value("${keycloak.provider.password-console:ZWJjMTViM2U4YjQ0MTQwZTI5ZjI1YWFk}")
    private String password_console;

    @Value("${keycloak.provider.client-secret:7i6w6w9yRbv2VOi0ksbLfdd1TnW5TTlb}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri:https://auth.chevere.ddns.net/realms/kynsoft/protocol/openid-connect/token}")
    private String tokenUri;

    @Value("${keycloak.provider.client-id:medinec}")
    private String client_id;

    @Value("${keycloak.provider.grant-type:password}")
    private String grant_type;


    public RealmResource getRealmResource() {



        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(server_url)
                .realm(realm_master)
                .clientId(admin_clic)
                .username(user_console)
                .password(password_console)
                .clientSecret(client_secret)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
        return keycloak.realm(realm_name);
    }

    public UsersResource getUserResource() {
        //Definir accion
        System.err.println("#######################################################");
        System.err.println("#######################################################");
        System.err.println("getUserResource");
        System.err.println("#######################################################");
        System.err.println("#######################################################");
        RealmResource realmResource = getRealmResource();
        System.err.println("#######################################################");
        System.err.println("resource");
        System.err.println("#######################################################");
        System.err.println("#######################################################");
        return realmResource.users();


    }
}
