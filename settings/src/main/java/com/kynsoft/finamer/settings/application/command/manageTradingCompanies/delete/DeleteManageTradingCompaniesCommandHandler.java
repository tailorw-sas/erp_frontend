package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageTradingCompaniesCommandHandler implements ICommandHandler<DeleteManageTradingCompaniesCommand> {

    private final IManageTradingCompaniesService service;

    public DeleteManageTradingCompaniesCommandHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageTradingCompaniesCommand command) {
        ManageTradingCompaniesDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
