package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageAgencyRequest {


    private UUID id;
    private String name;

}
