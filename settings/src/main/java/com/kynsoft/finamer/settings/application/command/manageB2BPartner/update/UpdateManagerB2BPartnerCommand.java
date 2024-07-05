package com.kynsoft.finamer.settings.application.command.manageB2BPartner.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerB2BPartnerCommand implements ICommand {

    private UUID id;
    private final String name;
    private final String description;
    private final Status status;
    private final String url;
    private final String ip;
    private final String userName;
    private final String password;
    private final String token;
    private final UUID b2BPartnerType;

    public UpdateManagerB2BPartnerCommand(UUID id, String description, String name, Status status, String url, String ip,
                                          String userName, String password, String token, UUID manageB2BPartnerType) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
        this.url = url;
        this.ip = ip;
        this.userName = userName;
        this.password = password;
        this.token = token;
        b2BPartnerType = manageB2BPartnerType;
    }

    public static UpdateManagerB2BPartnerCommand fromRequest(UpdateManagerB2BPartnerRequest request, UUID id) {
        return new UpdateManagerB2BPartnerCommand(
                id,
                request.getDescription(),
                request.getName(),
                request.getStatus(),
                request.getUrl(),
                request.getIp(),
                request.getUserName(),
                request.getPassword(),
                request.getToken(),
                request.getB2BPartnerType()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerB2BPartnerMessage(id);
    }
}
