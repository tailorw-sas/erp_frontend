package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerMerchantHotelEnrolleRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private UUID managerHotel;
    private String enrrolle;
    private String key;
    private String description;
    private Status status;
}
