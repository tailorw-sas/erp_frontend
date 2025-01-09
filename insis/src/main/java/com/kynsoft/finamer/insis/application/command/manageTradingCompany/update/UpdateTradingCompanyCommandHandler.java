package com.kynsoft.finamer.insis.application.command.manageTradingCompany.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateTradingCompanyCommandHandler implements ICommandHandler<UpdateTradingCompanyCommand> {

    private final IManageTradingCompanyService service;

    public UpdateTradingCompanyCommandHandler(IManageTradingCompanyService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateTradingCompanyCommand command) {
        ManageTradingCompanyDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCompany, command.getCompany(), dto.getCompany(), update::setUpdate);

        dto.setUpdatedAt(command.getUpdatedAt());

        service.update(dto);
    }
}
