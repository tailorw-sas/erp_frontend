package com.kynsoft.finamer.settings.application.command.managerMerchant.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.managerMerchant.ManagerMerchantCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerMerchant.ManagerMerchantCodeRule;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerMerchantCommandHandler implements ICommandHandler<CreateManagerMerchantCommand> {

    private final IManagerMerchantService service;
    private final IManagerB2BPartnerService serviceB2BPartner;

    public CreateManagerMerchantCommandHandler(IManagerMerchantService service,
                                               IManagerB2BPartnerService serviceB2BPartner) {
        this.service = service;
        this.serviceB2BPartner = serviceB2BPartner;
    }

    @Override
    public void handle(CreateManagerMerchantCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getB2bPartner(), "id", "B2B Partner ID cannot be null."));
        ManagerB2BPartnerDto managerB2BPartnerDto = this.serviceB2BPartner.findById(command.getB2bPartner());

        RulesChecker.checkRule(new ManagerMerchantCodeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerMerchantCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerMerchantDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                managerB2BPartnerDto,
                command.getDefaultm(),
                command.getStatus()
        ));
    }
}
