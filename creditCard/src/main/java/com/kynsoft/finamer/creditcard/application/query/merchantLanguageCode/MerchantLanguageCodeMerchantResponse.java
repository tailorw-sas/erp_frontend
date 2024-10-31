package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MerchantLanguageCodeMerchantResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;

    public MerchantLanguageCodeMerchantResponse(ManageMerchantDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }
}
