package com.kynsof.identity.application.command.module.create;

import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateModuleRequest {
    private String name;
    private String description;
    private ModuleStatus status;
    private String code;
}
