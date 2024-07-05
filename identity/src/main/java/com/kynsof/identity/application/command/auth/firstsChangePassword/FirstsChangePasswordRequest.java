package com.kynsof.identity.application.command.auth.firstsChangePassword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FirstsChangePasswordRequest {
    private String email;
    private String newPassword;
    private String oldPassword;
}
