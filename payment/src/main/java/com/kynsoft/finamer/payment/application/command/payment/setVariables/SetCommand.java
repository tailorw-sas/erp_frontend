package com.kynsoft.finamer.payment.application.command.payment.setVariables;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetCommand implements ICommand {

    public SetCommand() {
    }

    @Override
    public ICommandMessage getMessage() {
        return new SetMessage();
    }
}
