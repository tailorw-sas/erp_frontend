package com.kynsof.identity.application.query.businessModule.search;

import com.kynsof.identity.application.query.business.search.BusinessResponse;
import com.kynsof.identity.application.query.module.getbyid.ModuleResponse;
import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BusinessModuleResponse implements IResponse {
    private UUID id;
    private BusinessResponse businessResponse;
    private ModuleResponse moduleResponse;

    public BusinessModuleResponse(BusinessModuleDto businessModuleDto) {
        this.id = businessModuleDto.getId();
        this.businessResponse = new BusinessResponse(businessModuleDto.getBusiness());
        this.moduleResponse = new ModuleResponse(businessModuleDto.getModule());
    }

}
