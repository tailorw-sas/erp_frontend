package com.kynsof.identity.application.command.user.update;

import com.kynsof.share.core.domain.EUserType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateUserSystemCommand implements ICommand {
    private UUID id;
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private EUserType userType;
    private String image;

    public UpdateUserSystemCommand(UUID id, String userName, String email,
                                   String name, String lastName, EUserType userType, String image) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.userType = userType;
        this.image = image;
    }

    public static UpdateUserSystemCommand fromRequest(UUID id, UpdateUserSystemRequest request) {
        return new UpdateUserSystemCommand(
                id,
                request.getUserName(),
                request.getEmail(),
                request.getName(),
                request.getLastName(),
                request.getUserType(),
                request.getImage()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateUserSystemMessage();
    }
}
