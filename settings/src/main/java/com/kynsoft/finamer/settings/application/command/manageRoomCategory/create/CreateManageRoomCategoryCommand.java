package com.kynsoft.finamer.settings.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRoomCategoryCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private final String description;

    public CreateManageRoomCategoryCommand(String code, Status status, String name, String description) {
        this.description = description;
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
    }

    public CreateManageRoomCategoryCommand(UUID id, String code, Status status, String name, String description) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public static CreateManageRoomCategoryCommand fromRequest(CreateManageRoomCategoryRequest request){
        return new CreateManageRoomCategoryCommand(
                request.getCode(), request.getStatus(), request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRoomCategoryMessage(id);
    }
}
