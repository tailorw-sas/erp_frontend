package com.kynsoft.finamer.invoicing.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageHotelCommand implements ICommand {

    private UUID id;
    private String name;
    private UUID tradingCompany;
    private String status;
    private Boolean isVirtual;
    private boolean requiresFlatRate;
    private Boolean autoApplyCredit;

    public UpdateManageHotelCommand(UUID id, String name, UUID tradingCompany, String status, Boolean isVirtual,boolean requiresFlatRate, Boolean autoApplyCredit) {
        this.id = id;
        this.name = name;
        this.tradingCompany = tradingCompany;
        this.status = status;
        this.isVirtual = isVirtual;
        this.requiresFlatRate=requiresFlatRate;
        this.autoApplyCredit = autoApplyCredit;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageHotelMessage(id);
    }
}
