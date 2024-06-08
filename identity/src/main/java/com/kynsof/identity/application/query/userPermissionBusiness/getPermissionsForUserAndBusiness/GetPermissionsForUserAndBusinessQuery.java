package com.kynsof.identity.application.query.userPermissionBusiness.getPermissionsForUserAndBusiness;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GetPermissionsForUserAndBusinessQuery implements IQuery {

    private final UUID userId;
    private final UUID businessId;

    public GetPermissionsForUserAndBusinessQuery(UUID userId, UUID businessId) {

        this.userId = userId;
        this.businessId = businessId;
    }

}
