package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerMerchantConfig;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ManageMerchantResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private ManagerB2BPartnerResponse b2bPartner;
    private Boolean defaultm;
    private Status status;
    private ManagerMerchantConfigDto merchantConfigResponse;

    public ManageMerchantResponse(ManageMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.b2bPartner = new ManagerB2BPartnerResponse(dto.getB2bPartner());
        this.defaultm = dto.getDefaultm();
        this.status = dto.getStatus();
    }

}
