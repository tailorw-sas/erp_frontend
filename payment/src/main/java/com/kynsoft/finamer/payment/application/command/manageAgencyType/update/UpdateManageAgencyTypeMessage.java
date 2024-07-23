package com.kynsoft.finamer.payment.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageAgencyTypeMessage implements ICommandMessage {

    private final UUID id;

    public UpdateManageAgencyTypeMessage(UUID id) {
        this.id = id;
    }
}
