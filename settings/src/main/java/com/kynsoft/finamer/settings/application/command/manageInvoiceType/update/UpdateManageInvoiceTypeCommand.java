package com.kynsoft.finamer.settings.application.command.manageInvoiceType.update;

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
public class UpdateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPolicy;

    public static UpdateManageInvoiceTypeCommand fromRequest(UpdateManageInvoiceTypeRequest request, UUID id){
        return new UpdateManageInvoiceTypeCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getEnabledToPolicy()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceTypeMessage(id);
    }
}
