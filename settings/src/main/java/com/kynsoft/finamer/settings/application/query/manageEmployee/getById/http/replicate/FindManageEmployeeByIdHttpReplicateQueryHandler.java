package com.kynsoft.finamer.settings.application.query.manageEmployee.getById.http.replicate;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.replicate.ManageEmployeeHttpReplicateResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageEmployeeByIdHttpReplicateQueryHandler implements IQueryHandler<FindManageEmployeeByIdHttpReplicateQuery, ManageEmployeeHttpReplicateResponse>  {

    private final IManageEmployeeService service;

    public FindManageEmployeeByIdHttpReplicateQueryHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public ManageEmployeeHttpReplicateResponse handle(FindManageEmployeeByIdHttpReplicateQuery query) {
        ManageEmployeeDto response = service.findById(query.getId());

        return new ManageEmployeeHttpReplicateResponse(response);
    }
}
