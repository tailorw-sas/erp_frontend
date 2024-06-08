package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
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
public class ManagerCountryResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    private ManagerLanguageResponse managerLanguage;
    private Status status;

    public ManagerCountryResponse(ManagerCountryDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.dialCode = dto.getDialCode();
        this.iso3 = dto.getIso3();
        this.isDefault = dto.getIsDefault();
        this.managerLanguage = dto.getManagerLanguage() != null ? new ManagerLanguageResponse(dto.getManagerLanguage()) : null;
        this.status = dto.getStatus();
    }

}
