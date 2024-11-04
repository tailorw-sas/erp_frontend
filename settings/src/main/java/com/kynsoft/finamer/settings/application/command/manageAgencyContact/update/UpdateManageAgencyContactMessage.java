package com.kynsoft.finamer.settings.application.command.manageAgencyContact.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyContactMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_MANAGE_AGENCY_CONTACT";
}
