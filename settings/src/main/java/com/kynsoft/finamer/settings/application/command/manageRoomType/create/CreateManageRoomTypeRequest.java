package com.kynsoft.finamer.settings.application.command.manageRoomType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRoomTypeRequest {


    private String code;
    private String description;
    private Status status;
    private String name;
    private UUID manageHotel;
}
