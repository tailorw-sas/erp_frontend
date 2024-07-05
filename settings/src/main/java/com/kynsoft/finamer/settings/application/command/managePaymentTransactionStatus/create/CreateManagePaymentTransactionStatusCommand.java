package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigatePaymentTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;


    public CreateManagePaymentTransactionStatusCommand(String code, Status status, String name, String description, Boolean requireValidation,List<UUID>  navigate ) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.requireValidation = requireValidation;
        this.navigate = navigate;
    }

    public static CreateManagePaymentTransactionStatusCommand fromRequest(
            CreateManagePaymentTransactionStatusRequest request) {
        return new CreateManagePaymentTransactionStatusCommand(
                request.getCode(),

                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getRequireValidation(),
                request.getNavigate()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentTransactionStatusMessage(id);
    }
}
