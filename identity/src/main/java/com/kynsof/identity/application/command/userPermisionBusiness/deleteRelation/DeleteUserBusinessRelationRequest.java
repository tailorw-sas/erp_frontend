package com.kynsof.identity.application.command.userPermisionBusiness.deleteRelation;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteUserBusinessRelationRequest {
    private UUID userId;
    private UUID businessId;
}
