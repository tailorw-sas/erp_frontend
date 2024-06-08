package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Credential {
    private UUID credentialId;
    private String code;
    private String channel;
    private String login;
    private String secretKey;

//    public Credential(UUID credentialId, String code, String channel, String login, String secretKey) {
//        this.credentialId = credentialId;
//        this.code = code;
//        this.channel = channel;
//        this.login = login;
//        this.secretKey = secretKey;
//    }
//
//    public UUID getCredentialId() {
//        return credentialId;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getChannel() {
//        return channel;
//    }
//
//    public String getLogin() {
//        return login;
//    }
//
//    public String getSecretKey() {
//        return secretKey;
//    }

}
