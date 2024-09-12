package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageTradingCompaniesCommand implements ICommand {

    private UUID id;
    private Boolean isApplyInvoice;
    private String cif;
    private String address;
    private String company;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageTradingCompaniesMessage(id);
    }
}
