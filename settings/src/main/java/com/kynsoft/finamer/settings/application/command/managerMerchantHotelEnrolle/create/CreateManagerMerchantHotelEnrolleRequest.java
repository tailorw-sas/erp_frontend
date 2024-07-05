package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerMerchantHotelEnrolleRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private UUID managerHotel;
    private String enrrolle;
    private String key;
    private String description;
    private Status status;
}
