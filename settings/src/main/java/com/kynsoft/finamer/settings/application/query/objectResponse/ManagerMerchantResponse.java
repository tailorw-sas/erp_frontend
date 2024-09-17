package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
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
public class ManagerMerchantResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private ManagerB2BPartnerResponse b2bPartner;
    private Boolean defaultm;
    private Status status;

    public ManagerMerchantResponse(ManagerMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.b2bPartner = new ManagerB2BPartnerResponse(dto.getB2bPartner());
        this.defaultm = dto.getDefaultm();
        this.status = dto.getStatus();
    }

}
