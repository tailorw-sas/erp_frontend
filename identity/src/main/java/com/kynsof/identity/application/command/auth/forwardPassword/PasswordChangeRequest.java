package com.kynsof.identity.application.command.auth.forwardPassword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordChangeRequest {
    private String email;
    private String newPassword;
    private String otp;
}
