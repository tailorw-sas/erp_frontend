package com.kynsoft.finamer.payment.application.command.manageAgencyType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyTypeMessage implements ICommandMessage {

    private final UUID id;

    public CreateManageAgencyTypeMessage(UUID id) {
        this.id = id;
    }
}
