package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantDto {
    private UUID id;
    private String code;
    private String description;
    private ManagerB2BPartnerDto b2bPartner;
    private Boolean defaultm;
    private Status status;
}