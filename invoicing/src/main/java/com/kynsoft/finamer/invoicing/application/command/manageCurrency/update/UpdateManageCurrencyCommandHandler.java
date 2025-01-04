package com.kynsoft.finamer.invoicing.application.command.manageCurrency.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCurrencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCurrencyCommandHandler implements ICommandHandler<UpdateManageCurrencyCommand> {

    private final IManageCurrencyService service;

    public UpdateManageCurrencyCommandHandler(IManageCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageCurrencyCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Currency ID cannot be null."));

        ManageCurrencyDto test = this.service.findById(command.getId());
        test.setName(command.getName());
        test.setStatus(command.getStatus());
        this.service.update(test);
    }

}
