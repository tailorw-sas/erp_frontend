package com.kynsoft.finamer.settings.application.command.manageReportParamType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageReportParamTypeCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String label;
    private Boolean hotel;
    private String source;
    private String description;

    public CreateManageReportParamTypeCommand(Status status, String name, String label, Boolean hotel, String source, String description) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.name = name;
        this.label = label;
        this.hotel = hotel;
        this.source = source;
        this.description = description;
    }

    public static CreateManageReportParamTypeCommand fromRequest(CreateManageReportParamTypeRequest request){
        return new CreateManageReportParamTypeCommand(
                request.getStatus(),
                request.getName(),
                request.getLabel(),
                request.getHotel(),
                request.getSource(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageReportParamTypeMessage(id);
    }
}
