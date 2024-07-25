package com.kynsoft.finamer.creditcard.domain.dto;

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
    private ManageMerchantDto manageMerchant;
    private ManageHotelDto manageHotel;
    private String enrolle;
    private String status;
}
