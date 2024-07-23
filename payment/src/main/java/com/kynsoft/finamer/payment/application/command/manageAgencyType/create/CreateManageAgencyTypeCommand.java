package com.kynsoft.finamer.payment.application.command.manageAgencyType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;

    public CreateManageAgencyTypeCommand(UUID id, String code, String name, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public static CreateManageAgencyTypeCommand fromRequest(CreateManageAgencyTypeRequest request) {
        return new CreateManageAgencyTypeCommand(
               request.getId(),  request.getCode(),  request.getName(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyTypeMessage(id);
    }
}
