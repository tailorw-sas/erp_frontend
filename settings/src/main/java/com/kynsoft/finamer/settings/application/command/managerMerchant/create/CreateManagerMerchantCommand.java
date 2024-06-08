package com.kynsoft.finamer.settings.application.command.managerMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerMerchantCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;

    public CreateManagerMerchantCommand(String code, String description, UUID b2bPartner, Boolean defaultm, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.b2bPartner = b2bPartner;
        this.defaultm = defaultm;
        this.status = status;
    }

    public static CreateManagerMerchantCommand fromRequest(CreateManagerMerchantRequest request) {
        return new CreateManagerMerchantCommand(
                request.getCode(),
                request.getDescription(),
                request.getB2bPartner(),
                request.getDefaultm(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerMerchantMessage(id);
    }
}
