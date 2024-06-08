package com.kynsoft.finamer.settings.application.query.manageDepartmentGroup.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageDepartmentGroupByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageDepartmentGroupByIdQuery(UUID id) {
        this.id = id;
    }

}
