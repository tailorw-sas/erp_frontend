package com.kynsoft.finamer.invoicing.application.command.manageRoomType.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRoomTypeRequest {


    private String code;
    private String description;
    private String name;
    private UUID manageHotel;
}
