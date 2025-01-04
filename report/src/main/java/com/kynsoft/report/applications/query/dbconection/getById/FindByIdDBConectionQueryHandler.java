package com.kynsoft.report.applications.query.dbconection.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConnectionService;
import org.springframework.stereotype.Component;

@Component
public class FindByIdDBConectionQueryHandler implements IQueryHandler<FindDBConectionByIdQuery, DBConectionResponse> {

    private final IDBConnectionService service;

    public FindByIdDBConectionQueryHandler(IDBConnectionService service) {
        this.service = service;
    }

    @Override
    public DBConectionResponse handle(FindDBConectionByIdQuery query) {
        DBConectionDto dto = this.service.findById(query.getId());
        return new DBConectionResponse(dto);
    }
}
