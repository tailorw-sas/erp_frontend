package com.kynsoft.finamer.payment.application.command.payment.setVariables;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetMessage implements ICommandMessage {

    private final String command = "EJECUTADO";

    public SetMessage() {}

}
