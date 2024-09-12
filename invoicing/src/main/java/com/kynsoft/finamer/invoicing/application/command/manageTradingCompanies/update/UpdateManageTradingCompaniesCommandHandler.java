package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;

import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageTradingCompaniesCommandHandler
        implements ICommandHandler<UpdateManageTradingCompaniesCommand> {

    private final IManageTradingCompaniesService service;

    public UpdateManageTradingCompaniesCommandHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageTradingCompaniesCommand command) {
        RulesChecker.checkRule(
                new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Trading Companies ID cannot be null."));

        ManageTradingCompaniesDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateBoolean(dto::setIsApplyInvoice, command.getIsApplyInvoice(), dto.getIsApplyInvoice(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCif,command.getCif(),dto.getCif(),update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAddress,command.getAddress(),dto.getAddress(),update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCompany,command.getCompany(),dto.getCompany(),update::setUpdate);

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

    private boolean updateLong(Consumer<Long> setter, Long newValue, Long oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}
