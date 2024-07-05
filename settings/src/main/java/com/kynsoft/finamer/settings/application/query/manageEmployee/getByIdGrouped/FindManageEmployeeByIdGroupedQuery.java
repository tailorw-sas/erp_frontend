package com.kynsoft.finamer.settings.application.query.manageEmployee.getByIdGrouped;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageEmployeeByIdGroupedQuery implements IQuery {

    private final UUID id;

    public FindManageEmployeeByIdGroupedQuery(UUID id) {
        this.id = id;
    }
}
