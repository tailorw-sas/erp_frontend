package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
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

    public static UpdateDBConectionCommand fromRequest(UpdateDBConectionRequest request, UUID id){
        return new UpdateDBConectionCommand(
                id, request.getUrl(), request.getUsername(), request.getPassword()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateDBConectionMessage(id);
    }
}
