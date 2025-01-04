package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateManageTradingCompaniesCommand implements ICommand {

    private UUID id;
    private String code;
    private Boolean isApplyInvoice;
    private String cif;
    private String address;
    private String company;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTradingCompaniesMessage(id);
    }
}
