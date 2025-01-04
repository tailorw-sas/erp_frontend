package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findLanguageByMerchant;

import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindLanguageByMerchantLanguageResponse {
    private final UUID id;
    private final String code;
    private final String name;

    public FindLanguageByMerchantLanguageResponse(ManageLanguageDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
