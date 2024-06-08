package com.kynsof.identity.application.command.auth.registry;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistryCommand implements ICommand {
    private String resul;
    private final String username;
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final List<String> roles;



    public RegistryCommand(String username, String email, String firstname, String lastname, String password, List<String> roles) {

        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.roles = roles;
    }


    @Override
    public ICommandMessage getMessage() {
        return new RegistryMessage(resul);
    }
}
