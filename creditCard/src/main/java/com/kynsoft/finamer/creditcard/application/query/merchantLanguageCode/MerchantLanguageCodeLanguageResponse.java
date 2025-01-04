package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode;

import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MerchantLanguageCodeLanguageResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;

    public MerchantLanguageCodeLanguageResponse(ManageLanguageDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }
}
