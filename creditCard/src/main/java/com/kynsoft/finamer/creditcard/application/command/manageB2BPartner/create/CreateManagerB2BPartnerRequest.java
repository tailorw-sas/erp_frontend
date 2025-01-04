package com.kynsoft.finamer.creditcard.application.command.manageB2BPartner.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerB2BPartnerRequest {

    private String code;
    private String name;
    private String description;
    private Status status;
    private String url;
    private String ip;
    private String userName;
    private String password;
    private String token;
    private UUID b2BPartnerType;
}
