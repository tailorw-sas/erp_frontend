package com.kynsof.identity.application.command.user.sendPassword;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendPasswordRequest {
    private UUID userId;
    private String newPassword;
}
