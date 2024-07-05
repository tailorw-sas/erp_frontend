package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType.ManagePaymentTransactionTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagePaymentTransactionTypeCommandHandler implements ICommandHandler<UpdateManagePaymentTransactionTypeCommand> {

    private final IManagePaymentTransactionTypeService service;

    public UpdateManagePaymentTransactionTypeCommandHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentTransactionTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManagePaymentTransactionTypeDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDefaultRemark, command.getDefaultRemark(), dto.getDefaultRemark(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setMinNumberOfCharacter, command.getMinNumberOfCharacter(), dto.getMinNumberOfCharacter(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCash, command.getCash(), dto.getCash(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setAgencyRateAmount, command.getAgencyRateAmount(), dto.getAgencyRateAmount(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setNegative, command.getNegative(), dto.getNegative(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setPolicyCredit, command.getPolicyCredit(), dto.getPolicyCredit(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRemarkRequired, command.getRemarkRequired(), dto.getRemarkRequired(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDeposit, command.getDeposit(), dto.getDeposit(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setApplyDeposit, command.getApplyDeposit(), dto.getApplyDeposit(), update::setUpdate);
        if (UpdateIfNotNull.updateBoolean(dto::setDefaults, command.getDefaults(), dto.getDefaults(), update::setUpdate)) {
            RulesChecker.checkRule(new ManagePaymentTransactionTypeDefaultMustBeUniqueRule(service, command.getId()));
        }

        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
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
