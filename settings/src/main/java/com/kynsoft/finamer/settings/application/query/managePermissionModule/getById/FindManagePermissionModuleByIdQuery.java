package com.kynsoft.finamer.settings.application.query.managePermissionModule.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagePermissionModuleByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagePermissionModuleByIdQuery(UUID id) {
        this.id = id;
    }

}
