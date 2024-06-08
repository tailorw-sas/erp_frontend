package com.kynsof.identity.application.query.module.getbyid;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindModuleByIdQueryHandler implements IQueryHandler<FindModuleByIdQuery, ModuleResponse>  {

    private final IModuleService service;

    public FindModuleByIdQueryHandler(IModuleService service) {
        this.service = service;
    }

    @Override
    public ModuleResponse handle(FindModuleByIdQuery query) {
        ModuleDto response = service.findById(query.getId());

        return new ModuleResponse(response);
    }
}
