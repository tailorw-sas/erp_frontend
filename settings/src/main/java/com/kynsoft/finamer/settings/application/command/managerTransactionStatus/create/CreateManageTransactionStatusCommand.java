package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTransactionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Set<NavigateTransactionStatus> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

    public CreateManageTransactionStatusCommand(String code, String description, String name, Set<NavigateTransactionStatus> navigate, Boolean enablePayment, Boolean visible, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.navigate = navigate;
        this.enablePayment = enablePayment;
        this.visible = visible;
        this.status = status;
    }

    public static CreateManageTransactionStatusCommand fromRequest(CreateManageTransactionStatusRequest request) {
        return new CreateManageTransactionStatusCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getNavigate(),
                request.getEnablePayment(),
                request.getVisible(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTransactionStatusMessage(id);
    }
}
