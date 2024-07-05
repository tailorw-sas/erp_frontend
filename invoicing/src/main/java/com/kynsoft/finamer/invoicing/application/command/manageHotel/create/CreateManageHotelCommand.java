package com.kynsoft.finamer.invoicing.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageHotelCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private UUID manageTradingCompany;

    public CreateManageHotelCommand(UUID id, String code, String name, UUID manageTradingCompany) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.manageTradingCompany = manageTradingCompany;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageHotelMessage(id);
    }
}
