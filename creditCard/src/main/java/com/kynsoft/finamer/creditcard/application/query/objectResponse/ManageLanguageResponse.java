package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManageLanguageResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;

    public ManageLanguageResponse(ManageLanguageDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
