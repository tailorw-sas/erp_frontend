package com.kynsoft.finamer.invoicing.application.command.manageContact.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageContactRequest {

    private String code;
    private String description;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;
}
