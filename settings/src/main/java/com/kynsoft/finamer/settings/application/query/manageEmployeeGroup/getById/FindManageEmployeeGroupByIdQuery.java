package com.kynsoft.finamer.settings.application.query.manageEmployeeGroup.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageEmployeeGroupByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageEmployeeGroupByIdQuery(UUID id) {
        this.id = id;
    }

}
