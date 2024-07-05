package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTradingCompaniesCommand implements ICommand {

    private UUID id;
    private String code;
    private Boolean isApplyInvoice;

    public CreateManageTradingCompaniesCommand(UUID id, String code, Boolean isApplyInvoice) {
        this.id = id;
        this.code = code;
        this.isApplyInvoice = isApplyInvoice;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTradingCompaniesMessage(id);
    }
}
