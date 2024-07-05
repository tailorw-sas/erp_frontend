package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageMerchantCommissionCommandHandler implements ICommandHandler<UpdateManageMerchantCommissionCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService manageCreditCardService;
    private final IManageMerchantCommissionService service;

    public UpdateManageMerchantCommissionCommandHandler(IManageMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService manageCreditCardService,
                                                        IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.manageCreditCardService = manageCreditCardService;
        this.service = service;
    }

    @Override
    public void handle(UpdateManageMerchantCommissionCommand command) {
        ManageMerchantCommissionDto existingCommission = this.service.findById(command.getId());
        ManageMerchantDto manageMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());
        ManageCreditCardTypeDto creditCardTypeDto = this.manageCreditCardService.findById(command.getManageCreditCartType());
        existingCommission.setManagerMerchant(manageMerchantDto);
        existingCommission.setManageCreditCartType(creditCardTypeDto);
        existingCommission.setCommission(command.getCommission());
        existingCommission.setCalculationType(command.getCalculationType());
        this.service.update(existingCommission);
    }
}