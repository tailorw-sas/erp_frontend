package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
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
public class ManageMerchantHotelEnrolleResponse implements IResponse {

    private UUID id;
    private ManagerMerchantResponse managerMerchant;
    private ManagerCurrencyResponse managerCurrency;
    private ManageHotelResponse managerHotel;

    private String enrrolle;
    private String key;
    private String description;
    private Status status;

    public ManageMerchantHotelEnrolleResponse(ManageMerchantHotelEnrolleDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? new ManagerMerchantResponse(dto.getManagerMerchant()) : null;
        this.managerCurrency = dto.getManagerCurrency() != null ? new ManagerCurrencyResponse(dto.getManagerCurrency()) : null;
        this.managerHotel = dto.getManagerHotel() != null ? new ManageHotelResponse(dto.getManagerHotel()) : null;
        this.enrrolle = dto.getEnrrolle();
        this.key = dto.getKey();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

}
