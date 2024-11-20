package com.kynsoft.finamer.payment.application.command.resourceType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageResourceTypeCommand implements ICommand {
    private UUID id;
    private String description;
    private String name;
    private Status status;
    private Boolean defaults;
    private boolean invoice;
    private boolean vcc;

    public UpdateManageResourceTypeCommand(UUID id, String name, String description, Status status, Boolean defaults, boolean invoice, boolean vcc) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
        this.defaults = defaults;
        this.invoice = invoice;
        this.vcc = vcc;
    }

    public static UpdateManageResourceTypeCommand fromRequest(UpdateManageResourceTypeRequest request, UUID id) {
        return new UpdateManageResourceTypeCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getDefaults(),
                request.isInvoice(),
                request.isVcc()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageResourceTypeMessage(id);
    }
}
