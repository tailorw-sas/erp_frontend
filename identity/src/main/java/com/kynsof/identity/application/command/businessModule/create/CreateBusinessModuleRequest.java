package com.kynsof.identity.application.command.businessModule.create;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateBusinessModuleRequest {
    private UUID idBusiness;
    private List<UUID> modules;
}
