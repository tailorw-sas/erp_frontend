package com.kynsoft.finamer.settings.application.command.manageAgencyContact.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteManageAgencyContactMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_MANAGE_AGENCY_CONTACT";
}
