package com.kynsoft.finamer.settings.application.query.manageEmployee.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageEmployeeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageEmployeeByIdQuery(UUID id) {
        this.id = id;
    }

}
