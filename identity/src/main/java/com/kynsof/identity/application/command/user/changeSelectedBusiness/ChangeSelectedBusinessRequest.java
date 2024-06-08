package com.kynsof.identity.application.command.user.changeSelectedBusiness;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeSelectedBusinessRequest {
    private UUID businessId;
}
