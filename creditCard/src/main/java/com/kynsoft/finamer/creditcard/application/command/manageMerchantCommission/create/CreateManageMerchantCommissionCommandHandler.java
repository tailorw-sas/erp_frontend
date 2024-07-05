package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommissionCommandHandler implements ICommandHandler<CreateManageMerchantCommissionCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCreditCardType;
    private final IManageMerchantCommissionService service;

    public CreateManageMerchantCommissionCommandHandler(IManageMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService serviceCreditCardType,
                                                        IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCreditCardType = serviceCreditCardType;
        this.service = service;
    }

    @Override
    public void handle(CreateManageMerchantCommissionCommand command) {
        
        ManageCreditCardTypeDto manageCreditCardTypeDto = this.serviceCreditCardType.findById(command.getManageCreditCartType());
        ManageMerchantDto manageMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

        ManageMerchantCommissionDto commissionDto = new ManageMerchantCommissionDto(
                command.getId(),
                manageMerchantDto,
                manageCreditCardTypeDto,
                command.getCommission(),
                command.getCalculationType()
        );

        // Save new commission
        service.create(commissionDto);
    }
}