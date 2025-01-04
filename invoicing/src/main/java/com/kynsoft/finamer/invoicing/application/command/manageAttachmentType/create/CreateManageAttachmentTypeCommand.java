package com.kynsoft.finamer.invoicing.application.command.manageAttachmentType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageAttachmentTypeCommand implements ICommand {

    private UUID id;
    private String code;

    private String name;
    private String status;
    private Boolean defaults;
    private boolean attachInvDefault;

    

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAttachmentTypeMessage(id);
    }
}
