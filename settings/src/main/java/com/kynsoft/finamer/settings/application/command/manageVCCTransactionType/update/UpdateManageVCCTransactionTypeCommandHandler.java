package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageVCCTransactionTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageVCCTransactionType.ProducerUpdateManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageVCCTransactionTypeCommandHandler implements ICommandHandler<UpdateManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;
    private final ProducerUpdateManageVCCTransactionTypeService producerUpdateManageVCCTransactionTypeService;

    public UpdateManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service,
                                                        ProducerUpdateManageVCCTransactionTypeService producerUpdateManageVCCTransactionTypeService) {
        this.service = service;
        this.producerUpdateManageVCCTransactionTypeService = producerUpdateManageVCCTransactionTypeService;
    }

    @Override
    public void handle(UpdateManageVCCTransactionTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManageVCCTransactionTypeDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDefaultRemark, command.getDefaultRemark(), dto.getDefaultRemark(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setMinNumberOfCharacter, command.getMinNumberOfCharacter(), dto.getMinNumberOfCharacter(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsActive, command.getIsActive(), dto.getIsActive(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setNegative, command.getNegative(), dto.getNegative(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsDefault, command.getIsDefault(), dto.getIsDefault(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setPolicyCredit, command.getPolicyCredit(), dto.getPolicyCredit(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRemarkRequired, command.getRemarkRequired(), dto.getRemarkRequired(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setSubcategory, command.getSubcategory(), dto.getSubcategory(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setOnlyApplyNet, command.getOnlyApplyNet(), dto.getOnlyApplyNet(), update::setUpdate);

        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManageVCCTransactionTypeService.update(new UpdateManageVCCTransactionTypeKafka(dto.getId(), dto.getName()));
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
