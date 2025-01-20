package com.kynsoft.finamer.creditcard.application.command.manageContact.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageContactCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageContactMessage(id);
    }
}
