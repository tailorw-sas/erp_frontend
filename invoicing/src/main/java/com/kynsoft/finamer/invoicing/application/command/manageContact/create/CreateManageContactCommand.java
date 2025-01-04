package com.kynsoft.finamer.invoicing.application.command.manageContact.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageContactCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;

    public CreateManageContactCommand(UUID id, String code, String description, String name, UUID manageHotel, String email, String phone, Integer position) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.manageHotel = manageHotel;
        this.email = email;
        this.phone = phone;
        this.position = position;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageContactMessage(id);
    }
}
