package com.kynsoft.finamer.settings.application.query.manageEmployee.getById.http.replicate;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageEmployeeByIdHttpReplicateQuery  implements IQuery {

    private final UUID id;

    public FindManageEmployeeByIdHttpReplicateQuery(UUID id) {
        this.id = id;
    }

}
