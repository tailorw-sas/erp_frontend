package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageInvoiceStatusCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPrint;
    private Boolean enabledToPropagate;
    private Boolean enabledToApply;
    private Boolean enabledToPolicy;
    private Boolean processStatus;
    private List<UUID> navigate;
    private Boolean showClone;

    public static UpdateManageInvoiceStatusCommand fromRequest(UpdateManageInvoiceStatusRequest request, UUID id){
        return new UpdateManageInvoiceStatusCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getEnabledToPrint(),
                request.getEnabledToPropagate(),
                request.getEnabledToApply(),
                request.getEnabledToPolicy(),
                request.getProcessStatus(),
                request.getNavigate(),
                request.getShowClone()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceStatusMessage(id);
    }
}
