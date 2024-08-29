package com.kynsoft.finamer.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManageHotelDto {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean applyByTradingCompany;
    private UUID manageTradingCompany;
    private Boolean autoApplyCredit;
}
