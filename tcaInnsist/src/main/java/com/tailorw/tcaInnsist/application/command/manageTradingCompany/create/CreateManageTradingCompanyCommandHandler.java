package com.tailorw.tcaInnsist.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateManageTradingCompanyCommandHandler implements ICommandHandler<CreateManageTradingCompanyCommand> {

    private final IManageTradingCompanyService tradingCompanyService;

    @Override
    public void handle(CreateManageTradingCompanyCommand command) {
        ManageTradingCompanyDto tradingCompany = new ManageTradingCompanyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getConnectionId()
        );
        tradingCompanyService.create(tradingCompany);
    }
}
