package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantConfigCommandHandler implements ICommandHandler<CreateManageMerchantConfigCommand> {
    @Autowired
    private IManageMerchantConfigService service;
    @Autowired
    private IManagerMerchantService merchantService;

    @Override
    public void handle(CreateManageMerchantConfigCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageMerchantUuid(), "id", "Manager Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageMerchantUuid(), "id", "Manager Merchant ID cannot be null."));

        ManagerMerchantDto managerMerchantDto = this.merchantService.findById(command.getManageMerchantUuid());

        service.create(new ManagerMerchantConfigDto(command.getUuid(),managerMerchantDto
                , command.getUrl(), command.getAltUrl(), command.getSuccessUrl(), command.getErrorUrl(),command.getDeclinedUrl(),
                command.getMerchantType(), command.getName(),command.getMethod(), command.getMerchantType()));
    }
}
