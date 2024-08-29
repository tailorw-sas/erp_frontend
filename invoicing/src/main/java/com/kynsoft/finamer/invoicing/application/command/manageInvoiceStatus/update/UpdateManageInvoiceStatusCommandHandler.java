package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageInvoiceStatusCommandHandler implements ICommandHandler<UpdateManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public UpdateManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageInvoiceStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Status ID cannot be null."));

        ManageInvoiceStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();


        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setShowClone, command.getShowClone(), dto.getShowClone(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }


}
