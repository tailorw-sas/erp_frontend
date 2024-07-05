package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageReportResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private String moduleId;
    private String moduleName;

    public ManageReportResponse(ManageReportDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.moduleId = dto.getModuleId();
        this.moduleName = dto.getModuleName();
    }
}
