package com.kynsoft.finamer.settings.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentSourceCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isBank;

    public CreateManagePaymentSourceCommand(String code, String description, Status status, String name, Boolean isBank) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.isBank = isBank;
    }

    public static CreateManagePaymentSourceCommand fromRequest(CreateManagePaymentSourceRequest request){
        return new CreateManagePaymentSourceCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getIsBank()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentSourceMessage(id);
    }
}
