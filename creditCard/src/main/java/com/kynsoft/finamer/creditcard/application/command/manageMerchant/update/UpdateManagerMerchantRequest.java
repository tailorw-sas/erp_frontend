package com.kynsoft.finamer.creditcard.application.command.manageMerchant.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerMerchantRequest {
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;
}
