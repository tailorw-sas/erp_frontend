package com.kynsoft.finamer.settings.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPolicy;

    public CreateManageInvoiceTypeCommand(String code, String description, Status status, String name, Boolean enabledToPolicy) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.enabledToPolicy = enabledToPolicy;
    }

    public static CreateManageInvoiceTypeCommand fromRequest(CreateManageInvoiceTypeRequest request){
        return new CreateManageInvoiceTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getEnabledToPolicy()
        );

    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTypeMessage(id);
    }
}
