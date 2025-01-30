package com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update.UpdateConnectionParamsMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateB2BPartnerTypeCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private String status;
    private LocalDateTime updatedAt;

    public CreateB2BPartnerTypeCommand(UUID id, String code, String name, String status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.updatedAt = null;
    }


    @Override
    public ICommandMessage getMessage() {
        return new UpdateConnectionParamsMessage(id);
    }
}
