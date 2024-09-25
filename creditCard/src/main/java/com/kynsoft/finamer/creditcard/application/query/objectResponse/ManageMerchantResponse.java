package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
    private TransactionDto transactionDtoResponse;

    public ManageMerchantResponse(ManageMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.b2bPartner = dto.getB2bPartner() != null ? new ManagerB2BPartnerResponse(dto.getB2bPartner()) : null;
        this.defaultm = dto.getDefaultm();
        this.status = dto.getStatus();
    }

}
