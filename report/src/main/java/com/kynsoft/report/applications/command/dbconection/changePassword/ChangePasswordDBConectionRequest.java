package com.kynsoft.report.applications.command.dbconection.changePassword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangePasswordDBConectionRequest {

    private String oldPassword;
    private String newPassword;
}
