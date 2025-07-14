package com.kynsoft.finamer.insis.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageEmployeeCommand implements ICommand {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime updatedAt;
    private List<UUID> hotels;
    private List<UUID> agencies;

    public UpdateManageEmployeeCommand(UUID id, String firstName, String lastName, String email, List<UUID> hotels, List<UUID> agencies){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        this.hotels = hotels;
        this.agencies = agencies;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageEmployeeMessage(id);
    }
}
