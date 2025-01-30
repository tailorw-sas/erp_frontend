package com.tailorw.tcaInnsist.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTradingCompanyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private UUID connectionId;

    public CreateManageTradingCompanyCommand(UUID id, String code, String name, UUID connectionId){
        this.id = id;
        this.code = code;
        this.name = name;
        this.connectionId = connectionId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTradingCompanyMessage(id);
    }
}
