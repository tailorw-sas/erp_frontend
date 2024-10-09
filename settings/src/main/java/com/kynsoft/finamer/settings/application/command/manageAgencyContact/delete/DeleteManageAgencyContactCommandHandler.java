package com.kynsoft.finamer.settings.application.command.manageAgencyContact.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyContactService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAgencyContactCommandHandler implements ICommandHandler<DeleteManageAgencyContactCommand> {

    private final IManageAgencyContactService agencyContactService;

    public DeleteManageAgencyContactCommandHandler(IManageAgencyContactService agencyContactService) {
        this.agencyContactService = agencyContactService;
    }

    @Override
    public void handle(DeleteManageAgencyContactCommand command) {
        ManageAgencyContactDto dto = this.agencyContactService.findById(command.getId());
        this.agencyContactService.delete(dto);
    }
}
