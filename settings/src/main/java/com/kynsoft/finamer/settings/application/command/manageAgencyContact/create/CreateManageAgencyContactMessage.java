package com.kynsoft.finamer.settings.application.command.manageAgencyContact.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageAgencyContactMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MANAGE_AGENCY_CONTACT";
}
