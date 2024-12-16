package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantRequest {
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;
}
