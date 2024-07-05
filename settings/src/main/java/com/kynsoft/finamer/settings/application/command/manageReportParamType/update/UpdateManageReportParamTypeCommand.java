package com.kynsoft.finamer.settings.application.command.manageReportParamType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageReportParamTypeCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String label;
    private Boolean hotel;
    private String source;
    private String description;

    public static UpdateManageReportParamTypeCommand fromRequest(UpdateManageReportParamTypeRequest request, UUID id){
        return new UpdateManageReportParamTypeCommand(
                id,
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
        return new UpdateManageReportParamTypeMessage(id);
    }
}
