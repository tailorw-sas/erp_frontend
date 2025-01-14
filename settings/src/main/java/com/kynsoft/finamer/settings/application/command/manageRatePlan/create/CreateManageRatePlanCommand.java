package com.kynsoft.finamer.settings.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRatePlanCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private UUID hotel;
    private String description;
    private Status status;

    public CreateManageRatePlanCommand(String code, String description, String name,
                                       UUID hotel, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.hotel = hotel;
        this.status = status;
    }

    public CreateManageRatePlanCommand(UUID id, String code, String description, String name,
                                       UUID hotel, Status status) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.hotel = hotel;
        this.status = status;
    }

    public static CreateManageRatePlanCommand fromRequest(CreateManageRatePlanRequest request) {
        return new CreateManageRatePlanCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getHotel(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRatePlanMessage(id);
    }
}
