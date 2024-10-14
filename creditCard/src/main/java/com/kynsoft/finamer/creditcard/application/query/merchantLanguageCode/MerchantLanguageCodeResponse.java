package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MerchantLanguageCodeResponse implements IResponse {

    private UUID id;
    private String name;
    private String merchantLanguage;
    private MerchantLanguageCodeLanguageResponse manageLanguage;
    private MerchantLanguageCodeMerchantResponse manageMerchant;

    public MerchantLanguageCodeResponse(MerchantLanguageCodeDto dto){
        this.id = dto.getId();
        this.name = dto.getName();
        this.merchantLanguage = dto.getMerchantLanguage();
        this.manageLanguage = dto.getManageLanguage() != null ? new MerchantLanguageCodeLanguageResponse(dto.getManageLanguage()): null;
        this.manageMerchant = dto.getManageMerchant() != null ? new MerchantLanguageCodeMerchantResponse(dto.getManageMerchant()) : null;
    }
}
