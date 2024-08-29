package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceStatusCommand implements ICommand {

    private UUID id;
    private String code;
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

    public CreateManageInvoiceStatusCommand(String code, String description, Status status,
                                            String name, Boolean enabledToPrint,
                                            Boolean enabledToPropagate, Boolean enabledToApply,
                                            Boolean enabledToPolicy, Boolean processStatus,
                                            List<UUID> navigate, Boolean showClone) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.enabledToPrint = enabledToPrint;
        this.enabledToPropagate = enabledToPropagate;
        this.enabledToApply = enabledToApply;
        this.enabledToPolicy = enabledToPolicy;
        this.processStatus = processStatus;
        this.navigate = navigate;
        this.showClone = showClone;
    }

    public static CreateManageInvoiceStatusCommand fromRequest(CreateManageInvoiceStatusRequest request){
        return new CreateManageInvoiceStatusCommand(
                request.getCode(),
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
        return new CreateManageInvoiceStatusMessage(id);
    }
}
