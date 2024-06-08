package com.kynsof.identity.application.command.user.changePassword;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String newPassword;
    private String otp;
}
