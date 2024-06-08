package com.kynsof.identity.application.command.module.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateModuleRequest {
    private String name;
    private String description;
}
