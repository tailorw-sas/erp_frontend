package com.kynsoft.report.applications.query.dbconection.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConectionService;
import org.springframework.stereotype.Component;

@Component
public class FindByIdDBConectionQueryHandler implements IQueryHandler<FindDBConectionByIdQuery, DBConectionResponse> {

    private final IDBConectionService service;

    public FindByIdDBConectionQueryHandler(IDBConectionService service) {
        this.service = service;
    }

    @Override
    public DBConectionResponse handle(FindDBConectionByIdQuery query) {
        DBConectionDto dto = this.service.findById(query.getId());
        return new DBConectionResponse(dto);
    }
}
