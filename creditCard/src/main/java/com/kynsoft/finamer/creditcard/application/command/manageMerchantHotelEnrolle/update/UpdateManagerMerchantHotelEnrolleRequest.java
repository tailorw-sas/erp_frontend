package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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
