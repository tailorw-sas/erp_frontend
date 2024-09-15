package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantConfigCommandHandler implements ICommandHandler<CreateManageMerchantConfigCommand> {

    private final IManageMerchantConfigService service;
    private final IManageMerchantService iManageMerchantService;

    public CreateManageMerchantConfigCommandHandler(IManageMerchantConfigService service, IManageMerchantService iManageMerchantService) {
        this.service = service;
        this.iManageMerchantService = iManageMerchantService;
    }

    @Override
    public void handle(CreateManageMerchantConfigCommand command) {
        ManageMerchantDto manageMerchantDto = iManageMerchantService.findById(command.getManageMerchant());
        service.create(new ManagerMerchantConfigDto(
                command.getId(),
                manageMerchantDto,
                command.getUrl(),
                command.getAltUrl(),
                command.getSuccessUrl(),
                command.getErrorUrl(),
                command.getDeclinedUrl(),
                command.getMerchantType(),
                command.getName(),
                command.getMethod(),
                command.getInstitutionCode(),
                command.getMerchantNumber(),
                command.getMerchantTerminal()
        ));
    }
}

