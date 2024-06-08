package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantCurrencyDto;
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
    private ManagerMerchantResponse managerMerchant;
    private ManagerCurrencyResponse managerCurrency;
    private Double value;
    private String description;

    public ManagerMerchantCurrencyResponse(ManagerMerchantCurrencyDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManagerMerchantResponse(dto.getManagerMerchant());
        this.managerCurrency = new ManagerCurrencyResponse(dto.getManagerCurrency());
        this.value = dto.getValue();
        this.description = dto.getDescription();
    }

}
