package com.kynsoft.finamer.settings.application.query.manageEmployee.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageEmployeeByIdQueryHandler implements IQueryHandler<FindManageEmployeeByIdQuery, ManageEmployeeResponse>  {

    private final IManageEmployeeService service;

    public FindManageEmployeeByIdQueryHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public ManageEmployeeResponse handle(FindManageEmployeeByIdQuery query) {
        ManageEmployeeDto response = service.findById(query.getId());

        return new ManageEmployeeResponse(response);
    }
}
