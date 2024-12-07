package com.kynsoft.finamer.payment.application.command.resourceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageResourceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean invoice;
    private boolean vcc;

    public CreateManageResourceTypeCommand(String code, String name, String description, Status status, Boolean defaults, boolean invoice, boolean vcc) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;
        this.defaults = defaults;
        this.invoice = invoice;
        this.vcc = vcc;
    }

    public static CreateManageResourceTypeCommand fromRequest(CreateManageResourceTypeRequest request) {
        return new CreateManageResourceTypeCommand(
               request.getCode(), request.getName(), request.getDescription(), request.getStatus(), request.getDefaults(), request.isInvoice(), request.isVcc()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageResourceTypeMessage(id);
    }
}
