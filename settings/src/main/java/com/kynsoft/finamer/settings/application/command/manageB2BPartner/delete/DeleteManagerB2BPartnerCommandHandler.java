package com.kynsoft.finamer.settings.application.command.manageB2BPartner.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerB2BPartnerCommandHandler implements ICommandHandler<DeleteManagerB2BPartnerCommand> {

    private final IManagerB2BPartnerService service;

    public DeleteManagerB2BPartnerCommandHandler(IManagerB2BPartnerService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerB2BPartnerCommand command) {
        ManagerB2BPartnerDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
