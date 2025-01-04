package com.kynsoft.finamer.settings.application.command.manageAgencyContact.update;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageAgencyContactRequest {

    private UUID manageAgency;
    private UUID manageRegion;
    private List<UUID> manageHotel;
    private String emailContact;
}
