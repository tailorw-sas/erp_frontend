package com.kynsoft.finamer.payment.application.command.manageClient.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageClientRequest {
    private String code;
    private String name;
}
