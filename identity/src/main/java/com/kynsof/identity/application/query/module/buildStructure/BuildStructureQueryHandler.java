package com.kynsof.identity.application.query.module.buildStructure;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildStructureQueryHandler implements IQueryHandler<BuildStructureQuery, ModuleBuildResponse>  {

    private final IBusinessModuleService service;

    public BuildStructureQueryHandler(IBusinessModuleService service) {
        this.service = service;
    }

    @Override
    public ModuleBuildResponse handle(BuildStructureQuery query) {
        List<ModuleDto> response = service.findModulesByBusinessId(query.getBusinessId());

        return new ModuleBuildResponse(response);
    }
}
