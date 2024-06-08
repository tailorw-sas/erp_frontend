package com.kynsoft.finamer.settings.application.command.managerMerchant.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerMerchantRequest {
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;
}
