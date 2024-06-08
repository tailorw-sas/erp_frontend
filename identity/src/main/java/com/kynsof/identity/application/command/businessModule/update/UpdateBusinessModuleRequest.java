package com.kynsof.identity.application.command.businessModule.update;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessModuleRequest {
    private UUID idBusiness;
    private Set<UUID> modules;
}
