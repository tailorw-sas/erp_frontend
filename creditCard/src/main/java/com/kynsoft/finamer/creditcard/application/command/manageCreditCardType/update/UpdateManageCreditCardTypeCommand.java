package com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageCreditCardTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Integer firstDigit;
    private Status status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageCreditCardTypeMessage(id);
    }
}
