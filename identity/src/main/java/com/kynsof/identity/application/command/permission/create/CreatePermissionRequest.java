package com.kynsof.identity.application.command.permission.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePermissionRequest {

    private String code;
    private String description;
    private UUID moduleId;
    private String action;
}
