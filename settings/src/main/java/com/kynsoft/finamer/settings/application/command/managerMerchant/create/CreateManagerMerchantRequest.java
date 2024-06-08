package com.kynsoft.finamer.settings.application.command.managerMerchant.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerMerchantRequest {
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;
}
