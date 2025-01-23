package com.tailorw.tcaInnsist.application.command.manageTradingCompany.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CreateManyManageTradingCompanyCommandHandler implements ICommandHandler<CreateManyManageTradingCompanyCommand> {

    private final IManageTradingCompanyService tradingCompanyService;
    private static final Logger log = LoggerFactory.getLogger(CreateManyManageTradingCompanyCommandHandler.class);

    @Override
    public void handle(CreateManyManageTradingCompanyCommand command) {
        List<ManageTradingCompanyDto> tradingCompanyDtos = command.getCreateManageTradingCompanyCommands()
                .stream()
                .map(createCommand -> {
                    return new ManageTradingCompanyDto(
                            createCommand.getId(),
                            createCommand.getCode(),
                            createCommand.getName(),
                            createCommand.getConnectionId()
                    );
                }).toList();
        tradingCompanyService.createMany(tradingCompanyDtos);
        log.info("Created many tradingCompanies");
    }
}
