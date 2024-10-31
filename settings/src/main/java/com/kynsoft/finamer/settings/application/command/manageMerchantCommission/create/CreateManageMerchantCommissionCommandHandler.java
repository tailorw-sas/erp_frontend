package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantCommissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.*;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.time.LocalDate;

import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission.ProducerReplicateManageMerchantCommissionService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommissionCommandHandler implements ICommandHandler<CreateManageMerchantCommissionCommand> {

    private final IManagerMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    private final ProducerReplicateManageMerchantCommissionService producerService;
    public CreateManageMerchantCommissionCommandHandler(IManagerMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService serviceCurrencyService,
                                                        IManageMerchantCommissionService service, ProducerReplicateManageMerchantCommissionService producerService) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
        this.producerService = producerService;
    }

    @Override
    public void handle(CreateManageMerchantCommissionCommand command) {
        // Validate non-null fields
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getFromDate(), "fromDate", "From Date cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getCommission(), "commission", "Commission cannot be null."));
        LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");
        // Retrieve related DTOs
        ManageCreditCardTypeDto manageCreditCardTypeDto = this.serviceCurrencyService.findById(command.getManageCreditCartType());
        ManagerMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

        RulesChecker.checkRule(new ManageMerchantCommissionMustNotOverlapRule(this.service,command.getId(), command.getManagerMerchant(), command.getManageCreditCartType(), command.getFromDate(), toDate, command.getCommission(), command.getCalculationType()));

        ManageMerchantCommissionDto commissionDto = new ManageMerchantCommissionDto(
                command.getId(),
                managerMerchantDto,
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
        this.producerService.create(new ReplicateManageMerchantCommissionKafka(command.getId(), command.getManagerMerchant(), command.getManageCreditCartType(), command.getCommission(), command.getCalculationType(), command.getFromDate(), toDate, command.getStatus().name()));
    }
}