package com.kynsoft.finamer.insis.application.command.manageHotel.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateHotelRequest {

    private String code;
    private boolean deleted;
    private String name;
    private String status;
    private UUID tradingCompany;
}
