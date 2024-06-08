package com.kynsof.identity.application.command.permission.update;

import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePermissionRequest {
    private String code;
    private String description;
    private UUID module;
    private PermissionStatusEnm status;
    private String action;
}
