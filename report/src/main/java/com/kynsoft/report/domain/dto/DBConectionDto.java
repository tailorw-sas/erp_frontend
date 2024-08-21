package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DBConectionDto {

    private UUID id;
    private String url;
    private String username;
    private String password;
    private String code;
    private String name;
    private Status status;
}
