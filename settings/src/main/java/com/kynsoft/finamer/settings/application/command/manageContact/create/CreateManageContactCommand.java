package com.kynsoft.finamer.settings.application.command.manageContact.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageContactCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;

    public CreateManageContactCommand(String code, String description, Status status, String name, UUID manageHotel, String email, String phone, Integer position) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.manageHotel = manageHotel;
        this.email = email;
        this.phone = phone;
        this.position = position;
    }

    public static CreateManageContactCommand fromRequest(CreateManageContactRequest request){
        return new CreateManageContactCommand(
                request.getCode(), request.getDescription(), request.getStatus(),
                request.getName(), request.getManageHotel(), request.getEmail(),
                request.getPhone(), request.getPosition()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageContactMessage(id);
    }
}
