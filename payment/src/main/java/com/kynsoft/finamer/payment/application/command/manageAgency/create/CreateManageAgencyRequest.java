package com.kynsoft.finamer.payment.application.command.manageAgency.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyRequest {

    private String code;
    private String name;
    private UUID id;
}
