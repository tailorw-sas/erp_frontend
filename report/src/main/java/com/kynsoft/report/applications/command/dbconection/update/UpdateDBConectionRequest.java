package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateDBConectionRequest {

    private String url;
    private String username;
    private String password;
    private String code;
    private String name;
    private Status status;
}
