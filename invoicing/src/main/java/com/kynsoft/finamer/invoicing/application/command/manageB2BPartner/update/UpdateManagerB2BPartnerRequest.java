package com.kynsoft.finamer.invoicing.application.command.manageB2BPartner.update;


import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerB2BPartnerRequest {

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
