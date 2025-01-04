package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchant.ManageMerchantCodeMustBeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchant.ManageMerchantCodeRule;
import com.kynsoft.finamer.creditcard.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommandHandler implements ICommandHandler<CreateManageMerchantCommand> {

    private final IManageMerchantService service;
    private final IManagerB2BPartnerService serviceB2BPartner;

    public CreateManageMerchantCommandHandler(IManageMerchantService service,
                                              IManagerB2BPartnerService serviceB2BPartner) {
        this.service = service;
        this.serviceB2BPartner = serviceB2BPartner;
    }

    @Override
    public void handle(CreateManageMerchantCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getB2bPartner(), "id", "B2B Partner ID cannot be null."));
        ManagerB2BPartnerDto managerB2BPartnerDto = this.serviceB2BPartner.findById(command.getB2bPartner());

        RulesChecker.checkRule(new ManageMerchantCodeRule(command.getCode()));
        RulesChecker.checkRule(new ManageMerchantCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageMerchantDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                managerB2BPartnerDto,
                command.getDefaultm(),
                command.getStatus()
        ));
    }
}
