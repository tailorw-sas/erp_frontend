package com.kynsoft.finamer.settings.application.command.manageContact.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageContactRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;
}
