package com.kynsoft.finamer.payment.application.command.manageAgencyType.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyTypeRequest {

    private UUID id;
    private String code;
    private String name;
    private String status;
}
