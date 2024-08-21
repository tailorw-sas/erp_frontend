package com.kynsoft.report.applications.command.dbconection.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDBConectionCommand implements ICommand {

    private UUID id;
    private String url;
    private String username;
    private String password;
    private final
    String code;
    private final
    String name;
    private final Status status;

    public CreateDBConectionCommand(String url, String username, String password, String code, String name, Status status) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.id = UUID.randomUUID();
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static CreateDBConectionCommand fromRequest(CreateDBConectionRequest request){
        return new CreateDBConectionCommand(
                request.getUrl(), request.getUsername(), request.getPassword(),
                request.getCode(), request.getName(), request.getStatus() );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateDBConectionMessage(id);
    }
}
