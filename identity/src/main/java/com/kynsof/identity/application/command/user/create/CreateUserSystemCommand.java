package com.kynsof.identity.application.command.user.create;

import com.kynsof.share.core.domain.EUserType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserSystemCommand implements ICommand {
    private UUID id;
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private final String password;
    private final EUserType userType;
    private final String image;


    public CreateUserSystemCommand(String userName, String email, String name, String lastName, String password, EUserType userType, String image) {
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.userType = userType;
        this.image = image;
    }

    public static CreateUserSystemCommand fromRequest(CreateUserSystemRequest request) {
        return new CreateUserSystemCommand(
                request.getUserName(),
                request.getEmail(),
                request.getName(),
                request.getLastName(), request.getPassword(),
                request.getUserType(), request.getImage() );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateUserSystemMessage(id);
    }
}
