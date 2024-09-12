package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageTradingCompaniesCommandHandler
        implements ICommandHandler<CreateManageTradingCompaniesCommand> {

    private final IManageTradingCompaniesService service;

    public CreateManageTradingCompaniesCommandHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageTradingCompaniesCommand command) {

        service.create(new ManageTradingCompaniesDto(
                command.getId(),
                command.getCode(),
                command.getIsApplyInvoice(),
                null,
                command.getCif(),
                command.getAddress(),
                command.getCompany()));
    }
}
