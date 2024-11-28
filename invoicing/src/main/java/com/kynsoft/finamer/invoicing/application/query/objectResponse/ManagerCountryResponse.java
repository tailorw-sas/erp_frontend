package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerCountryResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean isDefault;
    private Status status;
    private ManageLanguageResponse language;

    public ManagerCountryResponse(ManagerCountryDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.isDefault = dto.getIsDefault();
        this.status = dto.getStatus();
        this.language = dto.getManagerLanguage() != null ? new ManageLanguageResponse(dto.getManagerLanguage()) : null;
    }

}
