package com.kynsoft.finamer.settings.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeCreditMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeIncomeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeInvoiceMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType.ProducerReplicateManageInvoiceTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageInvoiceTypeCommandHandler implements ICommandHandler<UpdateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    private final ProducerReplicateManageInvoiceTypeService producerReplicateManageInvoiceTypeService;

    public UpdateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service,
                                                 ProducerReplicateManageInvoiceTypeService producerReplicateManageInvoiceTypeService) {
        this.service = service;
        this.producerReplicateManageInvoiceTypeService = producerReplicateManageInvoiceTypeService;
    }

    @Override
    public void handle(UpdateManageInvoiceTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Type ID cannot be null."));

        if (command.isInvoice()){
            RulesChecker.checkRule(new ManageInvoiceTypeInvoiceMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isIncome()){
            RulesChecker.checkRule(new ManageInvoiceTypeIncomeMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCredit()){
            RulesChecker.checkRule(new ManageInvoiceTypeCreditMustBeUniqueRule(this.service, command.getId()));
        }

        ManageInvoiceTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToPolicy, command.getEnabledToPolicy(), dto.getEnabledToPolicy(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIncome, command.isIncome(), dto.isIncome(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCredit, command.isCredit(), dto.isCredit(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setInvoice, command.isInvoice(), dto.isInvoice(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerReplicateManageInvoiceTypeService.create(new ReplicateManageInvoiceTypeKafka(
                    dto.getId(), 
                    dto.getCode(), 
                    dto.getName(), 
                    dto.isIncome(), 
                    dto.isCredit(), 
                    dto.isInvoice(), 
                    dto.getStatus().name(), 
                    dto.getEnabledToPolicy()
            ));
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
