package com.kynsoft.finamer.settings.application.command.manageReport.update;

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
public class UpdateManageReportCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private String moduleId;
    private String moduleName;

    public static UpdateManageReportCommand fromRequest(UpdateManageReportRequest request, UUID id){
        return new UpdateManageReportCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getModuleId(),
                request.getModuleName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageReportMessage(id);
    }

}
