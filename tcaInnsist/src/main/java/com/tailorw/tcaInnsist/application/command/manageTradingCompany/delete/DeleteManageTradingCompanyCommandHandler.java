package com.tailorw.tcaInnsist.application.command.manageTradingCompany.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteManageTradingCompanyCommandHandler implements ICommandHandler<DeleteManageTradingCompanyCommand> {

    private final IManageTradingCompanyService tradingCompanyService;

    @Override
    public void handle(DeleteManageTradingCompanyCommand command) {
        tradingCompanyService.delete(command.getId());
    }
}
