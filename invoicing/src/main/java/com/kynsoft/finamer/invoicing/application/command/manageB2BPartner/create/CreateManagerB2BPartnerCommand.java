package com.kynsoft.finamer.invoicing.application.command.manageB2BPartner.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateManagerB2BPartnerCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private String url;
    private String ip;
    private String userName;
    private String password;
    private String token;
    private UUID ManageB2BPartnerType;

    public CreateManagerB2BPartnerCommand(UUID id,String code, String description, String name, Status status, String url,
                                          String ip, String userName, String password, String token, UUID ManageB2BPartnerType) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
        this.url = url;
        this.ip = ip;
        this.userName = userName;
        this.password = password;
        this.token = token;
        this.ManageB2BPartnerType = ManageB2BPartnerType;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerB2BPartnerMessage(id);
    }
}
