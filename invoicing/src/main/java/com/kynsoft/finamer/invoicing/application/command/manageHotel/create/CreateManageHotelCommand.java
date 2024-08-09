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
    private boolean isVirtual;
    private String status;
    private boolean requiresFlatRate;

    public CreateManageHotelCommand(UUID id, String code, String name, UUID manageTradingCompany,boolean isVirtual, String status,boolean requiresFlatRate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.manageTradingCompany = manageTradingCompany;
        this.isVirtual=isVirtual;
        this.status = status;
        this.requiresFlatRate =requiresFlatRate;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageHotelMessage(id);
    }
}
