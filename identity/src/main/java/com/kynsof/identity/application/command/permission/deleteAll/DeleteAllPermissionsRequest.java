package com.kynsof.identity.application.command.permission.deleteAll;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAllPermissionsRequest {
    private List<UUID> permissions;
}
