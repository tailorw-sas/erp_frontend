package com.kynsoft.report.applications.query.dbconection.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindDBConectionByIdQuery implements IQuery {

    private final UUID id;

    public FindDBConectionByIdQuery(UUID id) {
        this.id = id;
    }
}
