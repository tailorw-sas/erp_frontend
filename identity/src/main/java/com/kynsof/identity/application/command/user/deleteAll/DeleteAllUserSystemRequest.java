package com.kynsof.identity.application.command.user.deleteAll;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAllUserSystemRequest {
    private List<UUID> users;
}
