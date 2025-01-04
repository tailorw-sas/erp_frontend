package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerMerchantCurrencyResponse implements IResponse {

    private UUID id;
    private ManageMerchantResponse managerMerchant;
    private ManagerCurrencyResponse managerCurrency;
    private String value;
    private String description;
    private Status status;

    public ManagerMerchantCurrencyResponse(ManagerMerchantCurrencyDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManageMerchantResponse(dto.getManagerMerchant());
        this.managerCurrency = new ManagerCurrencyResponse(dto.getManagerCurrency());
        this.value = dto.getValue();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

}
