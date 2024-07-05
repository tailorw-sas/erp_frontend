package com.kynsoft.finamer.invoicing.application.command.manageRoomType.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageRoomTypeRequest {

    private String description;
    private String name;
    private UUID manageHotel;
}
