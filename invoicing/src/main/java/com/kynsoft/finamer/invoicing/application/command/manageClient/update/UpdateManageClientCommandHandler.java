package com.kynsoft.finamer.invoicing.application.command.manageClient.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;

import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageClientCommandHandler implements ICommandHandler<UpdateManageClientCommand> {

    private final IManagerClientService service;

    public UpdateManageClientCommandHandler(IManagerClientService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageClientCommand command) {

        ManageClientDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(),
                update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }

}
