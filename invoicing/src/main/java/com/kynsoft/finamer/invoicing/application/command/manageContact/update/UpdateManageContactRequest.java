package com.kynsoft.finamer.invoicing.application.command.manageContact.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageContactRequest {

    private String description;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;
}
