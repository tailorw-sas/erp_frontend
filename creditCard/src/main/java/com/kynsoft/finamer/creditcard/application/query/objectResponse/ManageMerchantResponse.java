package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantResponse implements IResponse {

    private UUID id;
    private String code;

    public ManageMerchantResponse(ManageMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
    }

}
