package com.kynsoft.report.applications.command.dbconection.create;

import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDBConectionRequest {

    private String url;
    private String username;
    private String password;
    private String code;
    private String name;
    private Status status;
}
