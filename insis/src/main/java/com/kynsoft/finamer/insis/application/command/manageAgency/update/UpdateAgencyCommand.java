package com.kynsoft.finamer.insis.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateAgencyCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;
    private LocalDateTime updatedAt;
    private String agencyAlias;

    public UpdateAgencyCommand(UUID id, String name, String agencyAlias, String status){
        this.id = id;
        this.name = name;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.agencyAlias = agencyAlias;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAgencyMessage(id);
    }
}
