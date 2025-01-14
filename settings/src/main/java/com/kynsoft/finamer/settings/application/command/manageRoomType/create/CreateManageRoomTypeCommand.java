package com.kynsoft.finamer.settings.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageRoomTypeCommand implements ICommand {

    private final UUID id;
    private final String code;
    private final String description;
    private final Status status;
    private final String name;
    private final UUID manageHotel;

    public CreateManageRoomTypeCommand(String code, String description, Status status,
                                       String name, UUID manageHotel) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.manageHotel = manageHotel;
    }

    public CreateManageRoomTypeCommand(UUID id, String code, String description, Status status,
                                       String name, UUID manageHotel) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.manageHotel = manageHotel;
    }

    public static CreateManageRoomTypeCommand fromRequest(CreateManageRoomTypeRequest request){
        return new CreateManageRoomTypeCommand(
                request.getCode(), request.getDescription(), request.getStatus(),
                request.getName(), request.getManageHotel()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRoomTypeMessage(id);
    }
}
