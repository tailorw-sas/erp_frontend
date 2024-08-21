package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateDBConectionCommand implements ICommand {

    private UUID id;
    private String url;
    private String username;
    private String password;
    private String code;
    private String name;
    private Status status;

    public static UpdateDBConectionCommand fromRequest(UpdateDBConectionRequest request, UUID id){
        return new UpdateDBConectionCommand(
                id, request.getUrl(), request.getUsername(), request.getPassword(),
                request.getCode(), request.getName(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateDBConectionMessage(id);
    }
}
