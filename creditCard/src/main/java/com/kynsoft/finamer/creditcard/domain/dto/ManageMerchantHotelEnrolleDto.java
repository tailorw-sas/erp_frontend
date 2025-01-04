package com.kynsoft.finamer.creditcard.domain.dto;

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
public class ManageMerchantHotelEnrolleDto {

    private UUID id;
    private ManageMerchantDto managerMerchant;
    private ManagerCurrencyDto managerCurrency;
    private ManageHotelDto managerHotel;

    private String enrrolle;
    private String key;
    private String description;
    private Status status;
}
