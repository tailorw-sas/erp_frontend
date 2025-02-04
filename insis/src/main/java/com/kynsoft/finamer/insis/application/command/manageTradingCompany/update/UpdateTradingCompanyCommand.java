package com.kynsoft.finamer.insis.application.command.manageTradingCompany.update;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateTradingCompanyCommand implements ICommand {

    private UUID id;
    private String company;
    private LocalDateTime updatedAt;


    public UpdateTradingCompanyCommand(UUID id, String company){
        this.id = id;
        this.company = company;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTradingCompanyMessage(id);
    }
}
