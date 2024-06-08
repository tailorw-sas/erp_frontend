package com.kynsof.identity.application.command.businessModule.deleteall;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAllBusinessModuleRequest {
    private List<UUID> businessModules;
}
