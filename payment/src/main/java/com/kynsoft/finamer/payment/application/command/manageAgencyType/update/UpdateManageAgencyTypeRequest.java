package com.kynsoft.finamer.payment.application.command.manageAgencyType.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageAgencyTypeRequest {


    private UUID id;
    private String name;
    private String status;

}
