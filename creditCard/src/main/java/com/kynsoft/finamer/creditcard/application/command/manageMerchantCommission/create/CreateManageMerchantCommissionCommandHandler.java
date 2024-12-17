package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantCommissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchantCommission.ManageMerchantCommissionMustNotOverlapRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreateManageMerchantCommissionCommandHandler implements ICommandHandler<CreateManageMerchantCommissionCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    public CreateManageMerchantCommissionCommandHandler(IManageMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService serviceCurrencyService,
                                                        IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
    }

    @Override
    public void handle(CreateManageMerchantCommissionCommand command) {
        // Validate non-null fields
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageMerchant(), "manageMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getFromDate(), "fromDate", "From Date cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getCommission(), "commission", "Commission cannot be null."));
        LocalDate toDate = command.getToDate() != null ? command.getToDate() : null;
        //LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");

        RulesChecker.checkRule(new ManageMerchantCommissionMustNotOverlapRule(this.service, command.getId(), command.getManageMerchant(), command.getManageCreditCartType(), command.getFromDate(), toDate, command.getCommission(), command.getCalculationType()));

        ManageCreditCardTypeDto manageCreditCardTypeDto = this.serviceCurrencyService.findById(command.getManageCreditCartType());
        ManageMerchantDto manageMerchantDto = this.serviceMerchantService.findById(command.getManageMerchant());

        ManageMerchantCommissionDto commissionDto = new ManageMerchantCommissionDto(
                command.getId(),
                manageMerchantDto,
                manageCreditCardTypeDto,
                command.getCommission(),
                command.getCalculationType(),
                command.getDescription(),
                command.getFromDate(),
                toDate,
                command.getStatus()
        );

        // Save new commission
        service.create(commissionDto);
    }
}