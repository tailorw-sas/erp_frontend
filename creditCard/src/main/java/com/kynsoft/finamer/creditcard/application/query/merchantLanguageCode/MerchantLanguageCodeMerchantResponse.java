package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MerchantLanguageCodeMerchantResponse {

    private UUID id;
    private String code;
    private String description;

    public MerchantLanguageCodeMerchantResponse(ManageMerchantDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
    }
}
