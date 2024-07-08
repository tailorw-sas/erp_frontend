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

    @Value("${keycloak.provider.server-url:https://authdev.tailorw.net/}")
    private String server_url;

    @Value("${keycloak.provider.realm-name:TailorwRealm}")
    private String realm_name;

    @Value("${keycloak.provider.realm-master:master}")
    private String realm_master;

    @Value("${keycloak.provider.admin-clic:admin-cli}")
    private String admin_clic;

    @Value("${keycloak.provider.user-console:admin}")
    private String user_console;

    @Value("${keycloak.provider.password-console:b65d2efd242f458794b84826d183c293}")
    private String password_console;

    @Value("${keycloak.provider.client-secret:c2IcfkbsS4q2K3iFRp1YELUBrabpDzGz}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri:https://authdev.tailorw.net/realms/TailorwRealm/protocol/openid-connect/token}")
    private String tokenUri;

    @Value("${keycloak.provider.client-id:login-app}")
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
