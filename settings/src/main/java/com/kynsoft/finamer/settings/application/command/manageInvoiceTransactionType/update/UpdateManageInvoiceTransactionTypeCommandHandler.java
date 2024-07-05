package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceTransactionTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType.ProducerUpdateManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<UpdateManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    private final ProducerUpdateManageInvoiceTransactionTypeService producerUpdateManageInvoiceTransactionTypeService;

    public UpdateManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service,
                                                            ProducerUpdateManageInvoiceTransactionTypeService producerUpdateManageInvoiceTransactionTypeService) {
        this.service = service;
        this.producerUpdateManageInvoiceTransactionTypeService = producerUpdateManageInvoiceTransactionTypeService;
    }

    @Override
    public void handle(UpdateManageInvoiceTransactionTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Transaction Type ID cannot be null."));

        ManageInvoiceTransactionTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsAgencyRateAmount, command.getIsAgencyRateAmount(), dto.getIsAgencyRateAmount(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsNegative, command.getIsNegative(), dto.getIsNegative(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsPolicyCredit, command.getIsPolicyCredit(), dto.getIsPolicyCredit(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsRemarkRequired, command.getIsRemarkRequired(), dto.getIsRemarkRequired(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setMinNumberOfCharacters, command.getMinNumberOfCharacters(), dto.getMinNumberOfCharacters(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDefaultRemark, command.getDefaultRemark(), dto.getDefaultRemark(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManageInvoiceTransactionTypeService.update(new UpdateManageInvoiceTransactionTypeKafka(dto.getId(), dto.getName()));
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
