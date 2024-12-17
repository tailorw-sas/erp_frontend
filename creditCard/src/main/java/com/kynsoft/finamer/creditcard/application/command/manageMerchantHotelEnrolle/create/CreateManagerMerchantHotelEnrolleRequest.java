package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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
