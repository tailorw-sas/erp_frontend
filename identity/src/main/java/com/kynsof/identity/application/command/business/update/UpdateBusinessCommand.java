package com.kynsof.identity.application.command.business.update;

import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private String logo;
    private String ruc;
    private EBusinessStatus status;
    private String address;

    public UpdateBusinessCommand(UUID id, String name,  String description,
                                 String logo, String ruc, EBusinessStatus status, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.ruc = ruc;
        this.status = status;
        this.address = address;
    }

    public static UpdateBusinessCommand fromRequest(UpdateBusinessRequest request, UUID id) {
        return new UpdateBusinessCommand(
                id,
                request.getName(),
                request.getDescription(), 
                request.getImage(),
                request.getRuc(), 
                request.getStatus(),
                request.getAddress()
                );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBusinessMessage(id);
    }
}
