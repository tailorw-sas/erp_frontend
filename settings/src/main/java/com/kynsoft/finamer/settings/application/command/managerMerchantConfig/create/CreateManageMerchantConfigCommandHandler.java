package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantConfigCommandHandler implements ICommandHandler<CreateManageMerchantConfigCommand> {

    private final IManageMerchantConfigService service;
    private final IManagerMerchantService merchantService;

    public CreateManageMerchantConfigCommandHandler(IManageMerchantConfigService service, IManagerMerchantService merchantService) {
        this.service = service;
        this.merchantService = merchantService;
    }

    @Override
    public void handle(CreateManageMerchantConfigCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageMerchant(), "id", "Manager Merchant ID cannot be null."));
    //    RulesChecker.checkRule(new ManagerMerchantConfigMustBeUniqueRule(this.service, command.getManageMerchant()) );

        ManagerMerchantDto managerMerchantDto = this.merchantService.findById(command.getManageMerchant());

        service.create(new ManagerMerchantConfigDto(
                command.getId(), 
                managerMerchantDto,
                 command.getUrl(), 
                command.getAltUrl(), 
                command.getSuccessUrl(), 
                command.getErrorUrl(), 
                command.getDeclinedUrl(),
                command.getMerchantType(), 
                command.getName(), 
                command.getMethod(), 
                command.getMerchantType(),
                command.getMerchantNumber(),
                command.getMerchantTerminal()
        ));
    }
}
