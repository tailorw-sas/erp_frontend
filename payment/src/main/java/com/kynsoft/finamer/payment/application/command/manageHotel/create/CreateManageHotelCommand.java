package com.kynsoft.finamer.payment.application.command.manageHotel.create;

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
    private String status;
    private Boolean applyByTradingCompany;
    private UUID manageTradingCompany;
    private Boolean autoApplyCredit;

    public CreateManageHotelCommand(UUID id, String code, String name, String status, Boolean applyByTradingCompany, UUID manageTradingCompany, Boolean autoApplyCredit) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.applyByTradingCompany = applyByTradingCompany;
        this.manageTradingCompany = manageTradingCompany;
        this.autoApplyCredit = autoApplyCredit;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageHotelMessage(id);
    }
}
