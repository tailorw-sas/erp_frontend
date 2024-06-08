package com.kynsoft.finamer.settings.application.command.managerMerchant.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerMerchantCommand implements ICommand {

    private UUID id;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private Status status;

    public UpdateManagerMerchantCommand(UUID id, String description, UUID b2bPartner, Boolean defaultm, Status status) {
        this.id = id;
        this.description = description;
        this.b2bPartner = b2bPartner;
        this.defaultm = defaultm;
        this.status = status;
    }

    public static UpdateManagerMerchantCommand fromRequest(UpdateManagerMerchantRequest request, UUID id) {
        return new UpdateManagerMerchantCommand(
                id,
                request.getDescription(),
                request.getB2bPartner(), 
                request.getDefaultm(), 
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMerchantMessage(id);
    }
}
