package com.kynsof.identity.application.query.userPermissionBusiness.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindByIdUserRoleBusinessQuery implements IQuery {

    private final UUID id;

    public FindByIdUserRoleBusinessQuery(UUID id) {
        this.id = id;
    }

}
