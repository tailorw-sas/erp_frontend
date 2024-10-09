package com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyContactService;
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
