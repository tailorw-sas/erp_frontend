package com.kynsof.identity.application.command.module.deleteAll;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAllModuleRequest {
    private List<UUID> modules;
}
